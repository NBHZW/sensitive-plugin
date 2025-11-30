package replacement_methods

import constants.ABNORMAL_NAME_LENGTH
import constants.DOUBLE_WORD_NAMES_LENGTH
import constants.MORE_WORD_NAMES_FRONT_KEEP_LENGTH
import constants.MORE_WORD_NAMES_TAIL_KEEP_LENGTH
import constants.RIGHT_RULE_LENGTH
import constants.SINGLE_WORD_NAMES_LENGTH
import constants.UN_MORE_WORD_NAMES_FRONT_KEEP_LENGTH
import utils.dealStrHide

/**
 * 姓名/网名昵称替换
 * 替换规则：
 * 单字名：姓保留，名替换（如 张三->张*）；
 * 双字名：姓保留，名首字替换（如 张三丰->张*丰）；
 * 复姓 / 四字名：保留前 1-2 字，后续替换（如 欧阳锋->欧阳*）
 */
class NameReplacement : AbstractReplacementMethod(){
    override fun doSensitive(
        fieldValue: Any?,
        replaceChar: Char,
        rule: Array<String>,
    ): String? {
        if(fieldValue == null) return handleNull() else if(fieldValue.toString().isEmpty()) return handleEmpty()
        val name = fieldValue as String
        // 如果rule为空或者rule非正确长度，则使用默认规则
        if(rule.isEmpty() || rule.size != RIGHT_RULE_LENGTH){
            val length = name.length
            return if(length <= ABNORMAL_NAME_LENGTH){
                name
            }else if(length == SINGLE_WORD_NAMES_LENGTH || length == DOUBLE_WORD_NAMES_LENGTH ){
                name.dealStrHide(UN_MORE_WORD_NAMES_FRONT_KEEP_LENGTH,length-2, replaceChar = replaceChar)
            }else{
                name.dealStrHide(MORE_WORD_NAMES_FRONT_KEEP_LENGTH,MORE_WORD_NAMES_TAIL_KEEP_LENGTH, replaceChar = replaceChar)
            }
        }else{
            // 使用用户配置的rule规则
            val (frontCharNum, tailCharNum, hiddenCharNum) =  rule
            return name.dealStrHide(frontCharNum.toInt(), tailCharNum.toInt(), hiddenCharNum.toInt(),replaceChar)
        }
    }
}