package com.impaktek.impakprint.parsers.text

class PrinterTextParserTag(tag: String) {

    val tagName: String
    val attributes: MutableMap<String, String> = mutableMapOf()
    val length: Int
    val isCloseTag: Boolean

    init {
        val processedTag = tag.trim()

        if (processedTag.startsWith("<") && processedTag.endsWith(">")) {
            length = processedTag.length
            val openTagIndex = processedTag.indexOf("<")
            val closeTagIndex = processedTag.indexOf(">")
            val nextSpaceIndex = processedTag.indexOf(" ")

            val tempTagName: String
            if (nextSpaceIndex in 0 until closeTagIndex) {
                tempTagName = processedTag.substring(openTagIndex + 1, nextSpaceIndex).lowercase()

                var attributesString = processedTag.substring(nextSpaceIndex, closeTagIndex).trim()
                while (attributesString.contains("='")) {
                    val equalPos = attributesString.indexOf("='")
                    val endPos = attributesString.indexOf("'", equalPos + 2)

                    if (equalPos == -1 || endPos == -1) break

                    val attributeName = attributesString.substring(0, equalPos).trim()
                    val attributeValue = attributesString.substring(equalPos + 2, endPos)

                    if (attributeName.isNotEmpty()) {
                        attributes[attributeName] = attributeValue
                    }

                    attributesString = attributesString.substring(endPos + 1).trim()
                }
            } else {
                tempTagName = processedTag.substring(openTagIndex + 1, closeTagIndex).lowercase()
            }

            isCloseTag = tempTagName.startsWith("/")
            tagName = if (isCloseTag) tempTagName.substring(1) else tempTagName
        } else {
            // Defaults when tag is invalid
            tagName = ""
            length = 0
            isCloseTag = false
        }
    }

    fun getAttribute(key: String): String? = attributes[key]
    fun hasAttribute(key: String): Boolean = attributes.containsKey(key)
}

