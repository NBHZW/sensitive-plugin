package replacement_methods

import constants.ABNORMAL_NAME_LENGTH
import constants.CARD_ID_DEFAULT_FRONT_KEEP_LENGTH
import constants.CARD_ID_DEFAULT_TAIL_KEEP_LENGTH
import constants.DOUBLE_WORD_NAMES_LENGTH
import constants.MORE_WORD_NAMES_FRONT_KEEP_LENGTH
import constants.MORE_WORD_NAMES_TAIL_KEEP_LENGTH
import constants.RIGHT_RULE_LENGTH
import constants.SINGLE_WORD_NAMES_LENGTH
import constants.UN_MORE_WORD_NAMES_FRONT_KEEP_LENGTH
import utils.dealStrHide

/**
 * 身份证号 / 驾驶证号 替换
 * 默认规则:
 * 保留前 6 位 + 后 4 位，中间 8 位替换（如 “110101199001011234”→“110101********1234”）
 */
class CardIdReplacement : AbstractReplacementMethod() {
    override fun doSensitive(
        fieldValue: Any?,
        replaceChar: Char,
        rule: Array<String>,
    ): String? {
        if(fieldValue == null) return handleNull() else if(fieldValue.toString().isEmpty()) return handleEmpty()
        val cardId = (fieldValue as String).trim()
        if(rule.isEmpty()){
            return cardId.dealStrHide(CARD_ID_DEFAULT_FRONT_KEEP_LENGTH,CARD_ID_DEFAULT_TAIL_KEEP_LENGTH, replaceChar = replaceChar)
        }else{
            // 使用用户配置的rule规则
            val (frontCharNum, tailCharNum, hiddenCharNum) =  rule
            return cardId.dealStrHide(frontCharNum.toInt(), tailCharNum.toInt(), hiddenCharNum.toInt(),replaceChar)
        }
    }
}