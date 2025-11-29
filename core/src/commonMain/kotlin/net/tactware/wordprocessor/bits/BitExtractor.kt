package net.tactware.wordprocessor.bits

/**
 * Interface for extracting specific bits from words and converting them to various data types.
 * Bit positions are 0-indexed from the least significant bit (LSB).
 */
interface BitExtractor {
    /**
     * Extracts a range of bits from a word value.
     *
     * @param value The word value to extract bits from
     * @param startBit The starting bit position (0-indexed, LSB = 0)
     * @param bitCount The number of bits to extract
     * @return The extracted bits as a Long value
     */
    fun extractBits(value: Long, startBit: Int, bitCount: Int): Long
    
    /**
     * Extracts bits and converts to a signed integer.
     *
     * @param value The word value to extract bits from
     * @param startBit The starting bit position
     * @param bitCount The number of bits to extract (max 32)
     * @return The extracted value as a signed Int
     */
    fun extractInt(value: Long, startBit: Int, bitCount: Int): Int
    
    /**
     * Extracts bits and converts to an unsigned integer.
     *
     * @param value The word value to extract bits from
     * @param startBit The starting bit position
     * @param bitCount The number of bits to extract (max 32)
     * @return The extracted value as an unsigned Int (returned as Long)
     */
    fun extractUInt(value: Long, startBit: Int, bitCount: Int): Long
    
    /**
     * Extracts bits and converts to a signed short.
     *
     * @param value The word value to extract bits from
     * @param startBit The starting bit position
     * @param bitCount The number of bits to extract (max 16)
     * @return The extracted value as a signed Short
     */
    fun extractShort(value: Long, startBit: Int, bitCount: Int): Short
    
    /**
     * Extracts bits and converts to an unsigned short.
     *
     * @param value The word value to extract bits from
     * @param startBit The starting bit position
     * @param bitCount The number of bits to extract (max 16)
     * @return The extracted value as an unsigned Short (returned as Int)
     */
    fun extractUShort(value: Long, startBit: Int, bitCount: Int): Int
    
    /**
     * Extracts bits and converts to a signed byte.
     *
     * @param value The word value to extract bits from
     * @param startBit The starting bit position
     * @param bitCount The number of bits to extract (max 8)
     * @return The extracted value as a signed Byte
     */
    fun extractByte(value: Long, startBit: Int, bitCount: Int): Byte
    
    /**
     * Extracts bits and converts to an unsigned byte.
     *
     * @param value The word value to extract bits from
     * @param startBit The starting bit position
     * @param bitCount The number of bits to extract (max 8)
     * @return The extracted value as an unsigned Byte (returned as Int)
     */
    fun extractUByte(value: Long, startBit: Int, bitCount: Int): Int
    
    /**
     * Extracts a single bit as a boolean.
     *
     * @param value The word value to extract the bit from
     * @param bitPosition The bit position (0-indexed, LSB = 0)
     * @return true if the bit is 1, false if 0
     */
    fun extractBoolean(value: Long, bitPosition: Int): Boolean
    
    /**
     * Extracts bits and interprets them as a 32-bit IEEE 754 float.
     *
     * @param value The word value to extract bits from
     * @param startBit The starting bit position
     * @return The extracted value as a Float
     */
    fun extractFloat(value: Long, startBit: Int): Float
    
    /**
     * Extracts bits and interprets them as a 64-bit IEEE 754 double.
     *
     * @param value The word value to extract bits from
     * @param startBit The starting bit position
     * @return The extracted value as a Double
     */
    fun extractDouble(value: Long, startBit: Int): Double
    
    /**
     * Extracts bits spanning multiple words.
     *
     * @param words The list of word values
     * @param startWord The starting word index (0-indexed)
     * @param startBit The starting bit position within the start word
     * @param bitCount The total number of bits to extract
     * @return The extracted bits as a Long value
     */
    fun extractBitsAcrossWords(words: List<Long>, startWord: Int, startBit: Int, bitCount: Int): Long
    
    /**
     * Extracts bits spanning a range of words.
     *
     * This is a convenience overload for cases where the field spans whole
     * words (optionally with a bit offset in the first word).
     *
     * The total number of extracted bits is derived from the detected word
     * size and the size of the provided word range. Implementations must
     * ensure that no more than 64 bits are returned.
     *
     * @param words The list of word values
     * @param wordRange The inclusive range of word indices (0-indexed)
     * @param startBit The starting bit position within the first word in the range
     * @return The extracted bits as a Long value
     */
    fun extractBitsAcrossWords(words: List<Long>, wordRange: IntRange, startBit: Int = 0): Long
}
