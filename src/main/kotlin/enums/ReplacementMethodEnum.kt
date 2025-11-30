package enums

import ReplacementMethod
import kotlin.reflect.KClass


enum class ReplacementMethodEnum(private var clazz: KClass<out ReplacementMethod>) {
    Default(AllReplacement::class),

}