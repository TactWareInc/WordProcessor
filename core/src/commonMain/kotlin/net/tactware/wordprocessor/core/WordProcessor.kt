package net.tactware.wordprocessor.core

/**
 * Core interface for word processing operations.
 * Handles conversion between byte arrays and words of various sizes.
 */
interface WordProcessor {
    /**
     * The word size configuration for this processor.
     */
    val wordSize: WordSize
    
    /**
     * The byte order configuration for this processor.
     */
    val byteOrder: ByteOrder
    
    /**
     * Extracts words from a byte array.
     * The byte array length must be aligned to the word size.
     *
     * @param bytes The byte array to process
     * @return A list of words represented as Long values
     * @throws IllegalArgumentException if the byte array length is not aligned to the word size
     */
    fun extractWords(bytes: ByteArray): List<Long>
    
    /**
     * Converts bytes at a specific offset to a single word.
     *
     * @param bytes The byte array containing the word data
     * @param offset The starting position in the byte array
     * @return The word value as a Long
     * @throws IndexOutOfBoundsException if there are insufficient bytes at the offset
     */
    fun bytesToWord(bytes: ByteArray, offset: Int): Long
    
    /**
     * Converts a word value to its byte representation.
     *
     * @param word The word value to convert
     * @return A byte array representing the word
     */
    fun wordToBytes(word: Long): ByteArray
    
    /**
     * Validates if a byte array can be processed by this word processor.
     *
     * @param bytes The byte array to validate
     * @return true if the byte array is valid for processing, false otherwise
     */
    fun validate(bytes: ByteArray): Boolean
}
