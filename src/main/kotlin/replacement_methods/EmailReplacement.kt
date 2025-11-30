package replacement_methods

import constants.EMAIL_LONG_LOCAL_PART_DEFAULT_FRONT_KEEP_LENGTH
import constants.EMAIL_SHORT_LOCAL_PART_DEFAULT_FRONT_KEEP_LENGTH
import utils.dealStrHide

/**
 * 邮箱替换
 * 默认规则：
 * 保留前缀前 1-2 位 +@及域名，中间替换
 * 如 “zhangsan@163.com”→“zhan***@163.com”“z***@163.com)
 */
class EmailReplacement : AbstractReplacementMethod() {
    override fun doSensitive(
        fieldValue: Any?,
        replaceChar: Char,
        rule: Array<String>,
    ): String? {
        if(fieldValue == null) return handleNull() else if(fieldValue.toString().isEmpty()) return handleEmpty()
        val email = (fieldValue as String).trim()
        val emailList = email.split("@")
        if(emailList.size != 2) throw RuntimeException("The message format does not comply with RFC 5322")
        val localPart = emailList[0]
        if(rule.isEmpty()){
            return if(localPart.length >2) localPart.dealStrHide(EMAIL_LONG_LOCAL_PART_DEFAULT_FRONT_KEEP_LENGTH,0, replaceChar = replaceChar)+"@"+emailList[1]
            else localPart.dealStrHide(EMAIL_SHORT_LOCAL_PART_DEFAULT_FRONT_KEEP_LENGTH,0, replaceChar = replaceChar)+"@"+emailList[1]
        }else{
            // 使用用户配置的rule规则
            val (frontCharNum, tailCharNum, hiddenCharNum) =  rule
            return email.dealStrHide(frontCharNum.toInt(), tailCharNum.toInt(), hiddenCharNum.toInt(),replaceChar)
        }
    }
}