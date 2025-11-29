package net.tactware.wordprocessor.core

/**
 * Defines supported word sizes for the word processor.
 *
 * @property bytes The number of bytes required to represent one word
 * @property bits The number of bits in one word
 */
enum class WordSize(val bytes: Int, val bits: Int) {
    /**
     * 16-bit word size (2 bytes)
     */
    WORD_16(2, 16),
    
    /**
     * 32-bit word size (4 bytes)
     */
    WORD_32(4, 32),
    
    /**
     * 64-bit word size (8 bytes)
     */
    WORD_64(8, 64);
    
    /**
     * Validates if the given byte array length is aligned to this word size.
     *
     * @param length The length of the byte array to validate
     * @return true if the length is a multiple of the word size in bytes, false otherwise
     */
    fun isAligned(length: Int): Boolean = length % bytes == 0
    
    /**
     * Calculates the number of complete words that can be extracted from the given byte count.
     *
     * @param byteCount The number of bytes
     * @return The number of complete words
     */
    fun wordCount(byteCount: Int): Int = byteCount / bytes
}
