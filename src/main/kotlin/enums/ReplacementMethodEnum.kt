package enums

import ReplacementMethod
import kotlin.reflect.KClass


enum class ReplacementMethodEnum(val clazz: KClass<out ReplacementMethod>) {
    Default(AllReplacement::class),

}