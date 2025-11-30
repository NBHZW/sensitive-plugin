interface ReplacementMethod {
    /**
     * 脱敏方法
     */
    fun sensitive(fieldValue: Any?, replaceChar: Char, rule: Array<String>?): String?

    /**
     * 是否清空字符串
     */
    fun isClean(): Boolean
}