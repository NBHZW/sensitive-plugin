package enums

import ReplacementMethod
import replacement_methods.CardIdReplacement
import replacement_methods.CardIdStrictReplacement
import replacement_methods.EmailReplacement
import replacement_methods.NameReplacement
import kotlin.reflect.KClass


enum class ReplacementMethodEnum(val clazz: KClass<out ReplacementMethod>) {
    Default(AllReplacement::class),
    Name(NameReplacement::class),
    CardIdStrict(CardIdStrictReplacement::class),
    CardId(CardIdReplacement::class),
    Email(EmailReplacement::class),
}