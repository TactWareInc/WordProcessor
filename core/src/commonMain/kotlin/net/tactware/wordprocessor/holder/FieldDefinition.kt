package net.tactware.wordprocessor.holder

/**
 * Represents a field definition in a message.
 *
 * @property name The name of the field
 * @property startIndex The starting word index (0-based)
 * @property endIndex The ending word index (0-based, inclusive)
 */
data class FieldDefinition(
    val name: String,
    val startIndex: Int,
    val endIndex: Int
) {
    /**
     * Checks if this field is a single word field.
     */
    val isSingleWord: Boolean
        get() = startIndex == endIndex
    
    /**
     * Gets the number of words in this field.
     */
    val wordCount: Int
        get() = endIndex - startIndex + 1
}