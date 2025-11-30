package replacement_methods

import constants.CARD_ID_STRICT_FRONT_KEEP_LENGTH
import constants.CARD_ID_STRICT_TAIL_KEEP_LENGTH
import utils.dealStrHide

/**
 * 身份证号 / 驾驶证号 严格替换
 * 默认规则:
 * 严格脱敏可保留前 3 位 + 后 3 位
 */
class CardIdStrictReplacement : AbstractReplacementMethod() {
    override fun doSensitive(
        fieldValue: Any?,
        replaceChar: Char,
        rule: Array<String>,
    ): String? {
        if(fieldValue == null) return handleNull() else if(fieldValue.toString().isEmpty()) return handleEmpty()
        val cardId = (fieldValue as String).trim()
        if(rule.isEmpty()){
            return cardId.dealStrHide(CARD_ID_STRICT_FRONT_KEEP_LENGTH,CARD_ID_STRICT_TAIL_KEEP_LENGTH, replaceChar = replaceChar)
        }else{
            // 使用用户配置的rule规则
            val (frontCharNum, tailCharNum, hiddenCharNum) =  rule
            return cardId.dealStrHide(frontCharNum.toInt(), tailCharNum.toInt(), hiddenCharNum.toInt(),replaceChar)
        }
    }
}