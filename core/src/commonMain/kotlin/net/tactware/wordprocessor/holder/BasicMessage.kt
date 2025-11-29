package net.tactware.wordprocessor.holder

import net.tactware.wordprocessor.core.WordProcessor



/**
 * Basic implementation of Message with field mapping support.
 *
 * @property words The list of words in the message
 * @property expectedWordCount The expected word count as defined in the ICD
 * @property processor The word processor used for byte array conversion
 * @property fieldDefinitions Map of field names to their definitions
 */
class BasicMessage(
    override val words: List<Long>,
    override val expectedWordCount: Int,
    private val processor: WordProcessor,
    private val fieldDefinitions: Map<String, FieldDefinition> = emptyMap()
) : Message {
    
    override val wordCount: Int
        get() = words.size
    
    /**
     * Validates that the message has the expected word count.
     *
     * @return true if the message has the expected word count, false otherwise
     */
    override fun isValid(): Boolean = wordCount == expectedWordCount
    
    /**
     * Gets a single word at the specified index (0-based).
     *
     * @param index The index of the word to retrieve
     * @return The word value at the specified index
     * @throws IndexOutOfBoundsException if the index is out of range
     */
    override fun getWord(index: Int): Long {
        require(index in words.indices) {
            "Index $index is out of bounds for word count $wordCount"
        }
        return words[index]
    }
    
    /**
     * Gets a range of words from startIndex (inclusive) to endIndex (inclusive).
     * Indices are 0-based.
     *
     * @param startIndex The starting index (inclusive)
     * @param endIndex The ending index (inclusive)
     * @return A list of words in the specified range
     * @throws IndexOutOfBoundsException if indices are out of range
     * @throws IllegalArgumentException if startIndex > endIndex
     */
    override fun getWordRange(startIndex: Int, endIndex: Int): List<Long> {
        require(startIndex <= endIndex) {
            "Start index $startIndex must be <= end index $endIndex"
        }
        require(startIndex in words.indices) {
            "Start index $startIndex is out of bounds for word count $wordCount"
        }
        require(endIndex in words.indices) {
            "End index $endIndex is out of bounds for word count $wordCount"
        }
        
        return words.subList(startIndex, endIndex + 1)
    }
    
    /**
     * Gets a range of words using 1-based indexing (as commonly used in ICDs).
     * For example, getWords1Based(5, 8) retrieves words 5, 6, 7, and 8.
     *
     * @param startWord The starting word number (1-based, inclusive)
     * @param endWord The ending word number (1-based, inclusive)
     * @return A list of words in the specified range
     * @throws IndexOutOfBoundsException if word numbers are out of range
     * @throws IllegalArgumentException if startWord > endWord or startWord < 1
     */
    override fun getWords1Based(startWord: Int, endWord: Int): List<Long> {
        require(startWord >= 1) {
            "Start word must be >= 1 (1-based indexing), got $startWord"
        }
        require(startWord <= endWord) {
            "Start word $startWord must be <= end word $endWord"
        }
        require(endWord <= wordCount) {
            "End word $endWord is out of bounds for word count $wordCount"
        }
        
        // Convert to 0-based indexing
        return getWordRange(startWord - 1, endWord - 1)
    }
    
    /**
     * Gets a named field from the message.
     * Field definitions are specific to the message implementation.
     *
     * @param fieldName The name of the field to retrieve
     * @return The word value for the specified field
     * @throws IllegalArgumentException if the field name is not recognized or is multi-word
     */
    override fun getField(fieldName: String): Long {
        val fieldDef = fieldDefinitions[fieldName]
            ?: throw IllegalArgumentException("Unknown field: $fieldName")
        
        require(fieldDef.isSingleWord) {
            "Field $fieldName spans multiple words (${fieldDef.wordCount}). Use getFieldRange() instead."
        }
        
        return getWord(fieldDef.startIndex)
    }
    
    /**
     * Gets a named field that spans multiple words.
     *
     * @param fieldName The name of the field to retrieve
     * @return A list of word values for the specified field
     * @throws IllegalArgumentException if the field name is not recognized
     */
    override fun getFieldRange(fieldName: String): List<Long> {
        val fieldDef = fieldDefinitions[fieldName]
            ?: throw IllegalArgumentException("Unknown field: $fieldName")
        
        return getWordRange(fieldDef.startIndex, fieldDef.endIndex)
    }
    
    /**
     * Converts this message back to a byte array.
     *
     * @return A byte array representation of all words
     */
    override fun toByteArray(): ByteArray {
        return words.flatMap { word ->
            processor.wordToBytes(word).toList()
        }.toByteArray()
    }
    
    override fun toString(): String {
        val validStatus = if (isValid()) "VALID" else "INVALID (expected $expectedWordCount words)"
        return "BasicMessage(wordCount=$wordCount, $validStatus, words=${words.joinToString(", ") { "0x%X".format(it) }})"
    }
}
