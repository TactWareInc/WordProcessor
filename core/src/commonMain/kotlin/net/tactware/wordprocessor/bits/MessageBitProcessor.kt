package net.tactware.wordprocessor.bits

import net.tactware.wordprocessor.holder.Message

/**
 * Processor for extracting bit-level fields from messages.
 *
 * @property message The message to extract bits from
 * @property bitExtractor The bit extractor to use (defaults to BasicBitExtractor)
 */
class MessageBitProcessor(
    private val message: Message,
    private val bitExtractor: BitExtractor = BasicBitExtractor()
) {
    
    /**
     * Extracts a range of bits from a specific word in the message.
     *
     * @param wordIndex The word index (0-based)
     * @param startBit The starting bit position (0-indexed, LSB = 0)
     * @param bitCount The number of bits to extract
     * @return The extracted bits as a Long value
     */
    fun extractBits(wordIndex: Int, startBit: Int, bitCount: Int): Long {
        val word = message.getWord(wordIndex)
        return bitExtractor.extractBits(word, startBit, bitCount)
    }
    
    /**
     * Extracts bits and converts to a signed integer.
     */
    fun extractInt(wordIndex: Int, startBit: Int, bitCount: Int): Int {
        val word = message.getWord(wordIndex)
        return bitExtractor.extractInt(word, startBit, bitCount)
    }
    
    /**
     * Extracts bits and converts to an unsigned integer.
     */
    fun extractUInt(wordIndex: Int, startBit: Int, bitCount: Int): Long {
        val word = message.getWord(wordIndex)
        return bitExtractor.extractUInt(word, startBit, bitCount)
    }
    
    /**
     * Extracts bits and converts to a signed short.
     */
    fun extractShort(wordIndex: Int, startBit: Int, bitCount: Int): Short {
        val word = message.getWord(wordIndex)
        return bitExtractor.extractShort(word, startBit, bitCount)
    }
    
    /**
     * Extracts bits and converts to an unsigned short.
     */
    fun extractUShort(wordIndex: Int, startBit: Int, bitCount: Int): Int {
        val word = message.getWord(wordIndex)
        return bitExtractor.extractUShort(word, startBit, bitCount)
    }
    
    /**
     * Extracts bits and converts to a signed byte.
     */
    fun extractByte(wordIndex: Int, startBit: Int, bitCount: Int): Byte {
        val word = message.getWord(wordIndex)
        return bitExtractor.extractByte(word, startBit, bitCount)
    }
    
    /**
     * Extracts bits and converts to an unsigned byte.
     */
    fun extractUByte(wordIndex: Int, startBit: Int, bitCount: Int): Int {
        val word = message.getWord(wordIndex)
        return bitExtractor.extractUByte(word, startBit, bitCount)
    }
    
    /**
     * Extracts a single bit as a boolean.
     */
    fun extractBoolean(wordIndex: Int, bitPosition: Int): Boolean {
        val word = message.getWord(wordIndex)
        return bitExtractor.extractBoolean(word, bitPosition)
    }
    
    /**
     * Extracts bits and interprets them as a 32-bit IEEE 754 float.
     */
    fun extractFloat(wordIndex: Int, startBit: Int): Float {
        val word = message.getWord(wordIndex)
        return bitExtractor.extractFloat(word, startBit)
    }
    
    /**
     * Extracts bits and interprets them as a 64-bit IEEE 754 double.
     */
    fun extractDouble(wordIndex: Int, startBit: Int): Double {
        val word = message.getWord(wordIndex)
        return bitExtractor.extractDouble(word, startBit)
    }
    
    /**
     * Extracts bits spanning multiple words.
     *
     * @param startWord The starting word index (0-indexed)
     * @param startBit The starting bit position within the start word
     * @param bitCount The total number of bits to extract
     * @return The extracted bits as a Long value
     */
    fun extractBitsAcrossWords(startWord: Int, startBit: Int, bitCount: Int): Long {
        val words = message.words
        return bitExtractor.extractBitsAcrossWords(words, startWord, startBit, bitCount)
    }
    
    /**
     * Extracts bits spanning an inclusive range of words.
     *
     * The total number of bits is derived from the detected word size and the
     * size of the provided word range. This is useful for fields that span
     * multiple consecutive words.
     *
     * @param wordRange The inclusive range of word indices (0-indexed)
     * @param startBit The starting bit position within the first word in the range
     * @return The extracted bits as a Long value
     */
    fun extractBitsAcrossWords(wordRange: IntRange, startBit: Int = 0): Long {
        val words = message.words
        return bitExtractor.extractBitsAcrossWords(words, wordRange, startBit)
    }
    
    /**
     * Extracts a fixed-length ASCII string spanning an inclusive range of words.
     *
     * This helper treats the extracted bits as a little-endian sequence of
     * 8-bit character codes, where the first 8 bits correspond to the first
     * character, the next 8 bits to the second, and so on.
     *
     * @param wordRange The inclusive range of word indices (0-indexed)
     * @param startBit The starting bit position within the first word in the range
     * @param length The number of characters (8-bit code units) to extract
     * @return The extracted String
     */
    fun extractStringAcrossWords(
        wordRange: IntRange,
        startBit: Int = 0,
        length: Int
    ): String {
        require(length > 0) { "Length must be positive" }
        
        val bitsNeeded = length * 8
        require(bitsNeeded <= 64) {
            "Cannot extract more than 64 bits ($length bytes) across words"
        }
        
        val words = message.words
        val bits = bitExtractor.extractBitsAcrossWords(
            words = words,
            startWord = wordRange.first,
            startBit = startBit,
            bitCount = bitsNeeded
        )
        
        // Convert little-endian bits → bytes → chars
        val bytes = ByteArray(length)
        for (i in 0 until length) {
            val shift = i * 8
            bytes[i] = ((bits ushr shift) and 0xFF).toByte()
        }
        
        val chars = CharArray(length)
        for (i in bytes.indices) {
            chars[i] = (bytes[i].toInt() and 0xFF).toChar()
        }
        
        return chars.concatToString()
    }
    
    /**
     * Extracts a bit field defined by a BitField definition.
     *
     * @param bitField The bit field definition
     * @return The extracted value as the appropriate type
     */
    fun extractField(bitField: BitField): Any {
        val word = message.getWord(bitField.wordIndex)
        
        return when (bitField.type) {
            BitFieldType.INT -> bitExtractor.extractInt(word, bitField.startBit, bitField.bitCount)
            BitFieldType.UINT -> bitExtractor.extractUInt(word, bitField.startBit, bitField.bitCount)
            BitFieldType.SHORT -> bitExtractor.extractShort(word, bitField.startBit, bitField.bitCount)
            BitFieldType.USHORT -> bitExtractor.extractUShort(word, bitField.startBit, bitField.bitCount)
            BitFieldType.BYTE -> bitExtractor.extractByte(word, bitField.startBit, bitField.bitCount)
            BitFieldType.UBYTE -> bitExtractor.extractUByte(word, bitField.startBit, bitField.bitCount)
            BitFieldType.BOOLEAN -> {
                require(bitField.bitCount == 1) { "Boolean fields must be exactly 1 bit" }
                bitExtractor.extractBoolean(word, bitField.startBit)
            }
            BitFieldType.FLOAT -> {
                require(bitField.bitCount == 32) { "Float fields must be exactly 32 bits" }
                bitExtractor.extractFloat(word, bitField.startBit)
            }
            BitFieldType.DOUBLE -> {
                require(bitField.bitCount == 64) { "Double fields must be exactly 64 bits" }
                bitExtractor.extractDouble(word, bitField.startBit)
            }
            BitFieldType.BITS -> bitExtractor.extractBits(word, bitField.startBit, bitField.bitCount)
        }
    }
    
    /**
     * Extracts multiple bit fields defined by a map of BitField definitions.
     *
     * @param bitFields Map of field names to BitField definitions
     * @return Map of field names to extracted values
     */
    fun extractFields(bitFields: Map<String, BitField>): Map<String, Any> {
        return bitFields.mapValues { (_, bitField) ->
            extractField(bitField)
        }
    }
    
    /**
     * Extracts a specific named bit field.
     *
     * @param fieldName The name of the field
     * @param bitFields Map of field names to BitField definitions
     * @return The extracted value
     */
    fun extractNamedField(fieldName: String, bitFields: Map<String, BitField>): Any {
        val bitField = bitFields[fieldName] ?: throw IllegalArgumentException("Unknown field: $fieldName")
        return extractField(bitField)
    }
}
