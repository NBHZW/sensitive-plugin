package entity

import annotation.SensitiveField
import kotlin.reflect.KCallable
import kotlin.reflect.KProperty1

class FieldInfo(var field: KProperty1<out Any, *>,
                var sensitiveField: SensitiveField?,
                var fieldAccessMethod: KCallable<*>?,) {

}