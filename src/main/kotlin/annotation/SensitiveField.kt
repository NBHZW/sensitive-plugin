package annotation

import enums.ReplacementMethodEnum

@Target(AnnotationTarget.FIELD)
@Retention(AnnotationRetention.RUNTIME)
annotation class SensitiveField(
    val method: ReplacementMethodEnum = ReplacementMethodEnum.Default,
    val replaceChar: Char = '*',
    val rule: Array<String> = []
)



