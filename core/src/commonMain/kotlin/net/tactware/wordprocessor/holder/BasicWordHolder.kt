package net.tactware.wordprocessor.holder

import net.tactware.wordprocessor.core.WordProcessor

/**
 * Basic implementation of WordHolder that stores a collection of words.
 *
 * @property words The list of words to hold
 * @property processor The word processor used for byte array conversion
 */
class BasicWordHolder(
    override val words: List<Long>,
    private val processor: WordProcessor
) : WordHolder {
    
    override val wordCount: Int
        get() = words.size
    
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
     * Converts this word holder back to a byte array.
     *
     * @return A byte array representation of all words
     */
    override fun toByteArray(): ByteArray {
        return words.flatMap { word ->
            processor.wordToBytes(word).toList()
        }.toByteArray()
    }
    
    override fun toString(): String {
        return "BasicWordHolder(wordCount=$wordCount, words=${words.joinToString(", ") { "0x%X".format(it) }})"
    }
}
