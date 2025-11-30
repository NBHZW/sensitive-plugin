package utils

object ReplaceUtils {
    /**
     * 处理替换隐藏
     * @param sensitiveData 敏感数据
     * @param frontCharNum 前面保留字符数
     * @param tailCharNum 后面保留字符数
     * @param hiddenCharNum 隐藏字符数
     * @return 隐藏后的数据 结构为 前保留字符 + 隐藏字符数 + 后保留字符数
     */
    fun dealStrHide(
        sensitiveData: String,
        frontCharNum: Int,
        tailCharNum: Int,
        hiddenCharNum: Int = 0,
        replaceChar: Char,
    ): String {
        if(sensitiveData.isEmpty()) return sensitiveData
        val result = sensitiveData.trim().let { value->
            val length = value.length
            if(frontCharNum <0 || tailCharNum < 0 || hiddenCharNum < 0 || frontCharNum+tailCharNum > length) return@let value
            // 原数据前半部分
            val result = StringBuilder()
            var replaceCharNum = hiddenCharNum
            val endIndex = length - tailCharNum

            // 前半部分
            if(frontCharNum in 0..<length){
                result.append(value.take(frontCharNum))
            }

            // 中间部分
            if(hiddenCharNum ==0) replaceCharNum = length - tailCharNum - frontCharNum
            result.append(replaceChar.toString().repeat(replaceCharNum))

            // 后半部分
            if (endIndex in 0..<length) {
                result.append(value.substring(endIndex))
            }
            result.toString()
        }
        return result
    }
}


fun String.dealStrHide(
    frontCharNum: Int,
    tailCharNum: Int,
    hiddenCharNum: Int = 0,
    replaceChar: Char,
): String {
    return ReplaceUtils.dealStrHide(this, frontCharNum, tailCharNum, hiddenCharNum, replaceChar)
}