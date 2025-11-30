import replacement_methods.AbstractReplacementMethod

/**
 * 全部替换
 * 默认返回为6个replaceChar,不会管rule规则
 */
class AllReplacement : AbstractReplacementMethod() {

    // 编译期常量，更高效且语义更明确
    private val DEFAULT_REPLACE_LENGTH = 6


    override fun doSensitive(
        fieldValue: Any?,
        replaceChar: Char,
        rule: Array<String>?
    ): String = replaceChar.toString().repeat(DEFAULT_REPLACE_LENGTH)
}