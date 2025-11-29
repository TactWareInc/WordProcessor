package net.tactware.wordprocessor.holder

/**
 * Interface for holding and accessing a collection of words.
 * Provides convenient methods for working with messages defined in ICDs.
 */
interface WordHolder {
    /**
     * The total number of words in this holder.
     */
    val wordCount: Int
    
    /**
     * All words in this holder as a list.
     */
    val words: List<Long>
    
    /**
     * Gets a single word at the specified index (0-based).
     *
     * @param index The index of the word to retrieve
     * @return The word value at the specified index
     * @throws IndexOutOfBoundsException if the index is out of range
     */
    fun getWord(index: Int): Long
    
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
    fun getWordRange(startIndex: Int, endIndex: Int): List<Long>
    
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
    fun getWords1Based(startWord: Int, endWord: Int): List<Long>
    
    /**
     * Converts this word holder back to a byte array.
     *
     * @return A byte array representation of all words
     */
    fun toByteArray(): ByteArray
}
