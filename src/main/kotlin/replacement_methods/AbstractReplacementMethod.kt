package replacement_methods

import ReplacementMethod

abstract class AbstractReplacementMethod: ReplacementMethod{

    protected open fun handleNull() : String?{
        return null
    }

    protected open fun handleEmpty() : String{
        return ""
    }

    abstract fun doSensitive(fieldValue: Any?, replaceChar: Char, rule: Array<String>?): String


    override fun sensitive(fieldValue: Any?, replaceChar: Char, rule: Array<String>?): String? {
        return when {
            fieldValue == null -> handleNull()
            fieldValue.toString().isEmpty() -> handleEmpty()
            else -> doSensitive(fieldValue, replaceChar, rule)
        }
    }

    override fun isClean(): Boolean {
        return true
    }
}