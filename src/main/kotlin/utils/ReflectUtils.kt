package utils

import annotation.SensitiveClass
import annotation.SensitiveField
import constants.DEFAULT_NESTED_LAYER_NUM
import constants.INVOKE_GET_PREFIX
import constants.INVOKE_IS_PREFIX
import entity.FieldInfo
import factory.ReplacementFactory
import java.lang.reflect.Modifier
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import kotlin.reflect.KCallable
import kotlin.reflect.KClass
import kotlin.reflect.KProperty1
import kotlin.reflect.full.declaredMembers
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.memberProperties
import kotlin.reflect.full.superclasses
import kotlin.reflect.jvm.javaField

/**
 * 反射工具类
 */
class ReflectUtils private constructor() {
    companion object {

        private val FIELD_INFO_CACHE_MAP = ConcurrentHashMap<String, MutableList<FieldInfo>>()

        fun reflectFieldsToStr(anyObject : Any?) : String?{
            return doReflectFieldsToStr(anyObject, DEFAULT_NESTED_LAYER_NUM)
        }

        private fun isClean(sensitiveField: SensitiveField?): Boolean {
            if(sensitiveField == null) {
                return false
            }
            val replacementMethod = ReplacementFactory.getReplacementMethod(sensitiveField.method)
            return replacementMethod?.isClean() ?: false
        }

        private fun sensitive(
            sensitiveField: SensitiveField?,
            propertyVal: Any?,
            nestedLayerNum: Int,
        ): String? {
            val strBuilder = StringBuilder()
            if( propertyVal is Collection<*> ) {
                for (item in propertyVal) {
                    strBuilder.append(sensitiveSingleObject(sensitiveField, item, nestedLayerNum))
                    strBuilder.append(",")
                }
                return strBuilder.toString()
            }
            if( propertyVal is MutableMap<*,*> && sensitiveField==null){
                strBuilder.append(propertyVal)
                return strBuilder.toString()
            }

            if (propertyVal is Array<*> && propertyVal.isArrayOf<Any>()) {
                for (item in propertyVal) {
                    strBuilder.append(sensitiveSingleObject(sensitiveField, item, nestedLayerNum))
                    strBuilder.append(",")
                }
                return strBuilder.toString()
            }

            return sensitiveSingleObject(sensitiveField, propertyVal, nestedLayerNum)
        }

        private fun sensitiveSingleObject(sensitiveField: SensitiveField?, item: Any?, nestedLayerNum: Int): String?{
            var temp = nestedLayerNum
            if(sensitiveField == null){
                if(item == null){
                    return "null"
                }
                if(temp<=0) return item.toString()
                temp--
                return doReflectFieldsToStr(item, temp)
            }

            ReplacementFactory.getReplacementMethod(sensitiveField.method)?.let {
                return it.sensitive(item, sensitiveField.replaceChar, sensitiveField.rule)
            }
            return null

        }

        private fun doReflectFieldsToStr(anyObject: Any?, nestedLayerNum: Int) : String?{
            if (anyObject == null) {
                return null
            } else if (anyObject is String || anyObject is Int || anyObject is Long ||
                anyObject is Float || anyObject is Double || anyObject is Boolean ||
                anyObject is Char || anyObject is Byte || anyObject is Short ||
                anyObject is Date) {
                return anyObject.toString()
            }
            // 获取类的注解 判断是否为需要敏感词脱敏得类
            val sensitiveClass = anyObject::class.annotations.find { it is SensitiveClass  }
            if(sensitiveClass == null) {
                return anyObject.toString()
            }
            // 拼接每个字段的toString方法
            val result = StringBuilder()
            result.append(anyObject::class.qualifiedName).append("{")
            val fieldInfoList : MutableList<FieldInfo> = getFieldInfoList(anyObject)
            for (fieldInfo in fieldInfoList) {
                val sensitiveField = fieldInfo.sensitiveField
                val javaField = fieldInfo.field.javaField
                if(javaField != null && Modifier.isStatic(javaField.modifiers)) {
                     continue
                }
                try{
                    val fieldName = fieldInfo.field.name
                    var propertyVal: Any? = null
                    if(fieldInfo.fieldAccessMethod != null){
                        try{
                            propertyVal = fieldInfo.fieldAccessMethod!!.call(anyObject)
                        }catch(e: IllegalAccessException){
                            fieldInfo.fieldAccessMethod = null
                        }
                    }
                    if(!isClean(sensitiveField)){
                        val replaceValue:String? = sensitive(sensitiveField,propertyVal,nestedLayerNum)
                        result.append(fieldName).append("=").append(replaceValue).append(",")
                    }
                }catch (e : Exception){
                    e.printStackTrace()
                }
            }
            result.append("}")
            return result.toString()


        }

        private fun getFieldInfoList(anyObject: Any): MutableList<FieldInfo> {
            val qualifiedName = anyObject::class.qualifiedName ?: return mutableListOf()
            val fieldInfoList = FIELD_INFO_CACHE_MAP[qualifiedName]
            if(fieldInfoList == null){
                val fieldInfoList = mutableListOf<FieldInfo>()
                appendFieldInfoList(anyObject::class,fieldInfoList)
                FIELD_INFO_CACHE_MAP[qualifiedName] = fieldInfoList
            }
            return fieldInfoList!!
        }

        private fun appendFieldInfoList(klass: KClass<out Any>, fieldInfoList: MutableList<FieldInfo>){
            if(Any::class.qualifiedName.equals(klass.qualifiedName)) return
            klass.memberProperties.forEach { field ->
                val sensitiveField = field.findAnnotation<SensitiveField>()
                val fieldAccessMethod = getFieldAccessMethod(field, klass)
                fieldInfoList.add(FieldInfo(field, sensitiveField, fieldAccessMethod))
            }
            val supperClass= klass.superclasses
            for (clazz in supperClass) {
                appendFieldInfoList(clazz, fieldInfoList)
            }

        }

        private fun getFieldAccessMethod(field: KProperty1<out Any, *>, klass: KClass<out Any>): KCallable<*>? {
            // 获取字段访问方法名称
            var methodName = getMethodName(field)
            // 获取字段访问方法
            var fieldAccessMethod = getMethodByName(methodName, klass)
            if(fieldAccessMethod == null && (Boolean::class.javaPrimitiveType == field.javaField?.type || Boolean::class.java == field.javaField?.type)){
                val fieldName = field.name
                methodName = INVOKE_GET_PREFIX + fieldName.take(1).uppercase() + fieldName.substring(1)
                fieldAccessMethod = getMethodByName(methodName, klass)
            }
            return fieldAccessMethod
        }

        private fun getMethodByName(methodName: String, klass: KClass<out Any>): KCallable<*>? {
            try{
                return klass.declaredMembers.find { it.name == methodName }
            }catch (e: NoSuchMethodException){

                // 遍历父类
                val supperClass= klass.superclasses
                for (clazz in supperClass) {
                    return getMethodByName( methodName,clazz)
                }

            }catch (e: Exception){
                println("getMethodByName error: $e")
            }
            return null
        }

        private fun getMethodName(field: KProperty1<out Any, *>): String {
            val fieldName = field.name
            val fieldClazz = field.javaField?.type

            return if (fieldClazz != null &&
                (fieldClazz == Boolean::class.java || fieldClazz == Boolean::class.javaPrimitiveType)
            ) {
                // 布尔类型字段：判断是否以 is 前缀开头
                if (fieldName.startsWith(INVOKE_IS_PREFIX)) {
                    fieldName
                } else {
                    INVOKE_IS_PREFIX + fieldName.replaceFirstChar { it.uppercase() }
                }
            } else {
                // 非布尔类型或无 javaField（如计算属性）：使用 get 前缀
                INVOKE_GET_PREFIX + fieldName.replaceFirstChar { it.uppercase() }
            }
        }
    }
}