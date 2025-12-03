package factory

import ReplacementMethod
import enums.ReplacementMethodEnum
import java.util.concurrent.ConcurrentHashMap

/**
 * 工厂类，生成替换策略类
 */
class ReplacementFactory private constructor(){
    companion object {
        private val replacementMap: MutableMap<ReplacementMethodEnum, ReplacementMethod> =
            ConcurrentHashMap(ReplacementMethodEnum.entries.size)

        init {
            ReplacementMethodEnum.entries.forEach { enum ->
                try {
                    val instance = enum.clazz.constructors.first { it.parameters.isEmpty() }.call()
                    replacementMap[enum] = instance
                } catch (e: Exception) {
                    when(e){
                        is NoSuchElementException -> { println("${enum.clazz.simpleName} 没有无参构造函数") }
                        is IllegalArgumentException , is InstantiationException -> { println("实例化 ${enum.clazz.simpleName} 失败：${e.message}") }
                        else -> { println("实例化 ${enum.clazz.simpleName} 失败：${e.message}") }
                    }

                    e.printStackTrace()
                }
            }
        }

        fun getReplacementMethod(replacementMethodEnum: ReplacementMethodEnum): ReplacementMethod? {
            return replacementMap[replacementMethodEnum]
        }
    }

}