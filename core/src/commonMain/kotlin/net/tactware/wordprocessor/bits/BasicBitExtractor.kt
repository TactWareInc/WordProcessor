package net.tactware.wordprocessor.bits

/**
 * Basic implementation of BitExtractor for extracting bits from words.
 * Bit positions are 0-indexed from the least significant bit (LSB).
 */
class BasicBitExtractor : BitExtractor {
    
    override fun extractBits(value: Long, startBit: Int, bitCount: Int): Long {
        require(startBit >= 0) { "Start bit must be non-negative" }
        require(bitCount > 0) { "Bit count must be positive" }
        require(startBit + bitCount <= 64) { "Bit range exceeds 64 bits" }
        
        // Create a mask with bitCount 1s
        val mask = if (bitCount == 64) -1L else (1L shl bitCount) - 1L
        
        // Shift value right to align start bit with LSB, then apply mask
        return (value ushr startBit) and mask
    }
    
    override fun extractInt(value: Long, startBit: Int, bitCount: Int): Int {
        require(bitCount <= 32) { "Cannot extract more than 32 bits for Int" }
        
        val extracted = extractBits(value, startBit, bitCount)
        
        // Check if sign bit is set (MSB of extracted bits)
        val signBit = 1L shl (bitCount - 1)
        return if ((extracted and signBit) != 0L) {
            // Negative number - sign extend
            (extracted or ((-1L) shl bitCount)).toInt()
        } else {
            extracted.toInt()
        }
    }
    
    override fun extractUInt(value: Long, startBit: Int, bitCount: Int): Long {
        require(bitCount <= 32) { "Cannot extract more than 32 bits for UInt" }
        return extractBits(value, startBit, bitCount)
    }
    
    override fun extractShort(value: Long, startBit: Int, bitCount: Int): Short {
        require(bitCount <= 16) { "Cannot extract more than 16 bits for Short" }
        
        val extracted = extractBits(value, startBit, bitCount)
        
        // Check if sign bit is set
        val signBit = 1L shl (bitCount - 1)
        return if ((extracted and signBit) != 0L) {
            // Negative number - sign extend
            (extracted or ((-1L) shl bitCount)).toShort()
        } else {
            extracted.toShort()
        }
    }
    
    override fun extractUShort(value: Long, startBit: Int, bitCount: Int): Int {
        require(bitCount <= 16) { "Cannot extract more than 16 bits for UShort" }
        return extractBits(value, startBit, bitCount).toInt()
    }
    
    override fun extractByte(value: Long, startBit: Int, bitCount: Int): Byte {
        require(bitCount <= 8) { "Cannot extract more than 8 bits for Byte" }
        
        val extracted = extractBits(value, startBit, bitCount)
        
        // Check if sign bit is set
        val signBit = 1L shl (bitCount - 1)
        return if ((extracted and signBit) != 0L) {
            // Negative number - sign extend
            (extracted or ((-1L) shl bitCount)).toByte()
        } else {
            extracted.toByte()
        }
    }
    
    override fun extractUByte(value: Long, startBit: Int, bitCount: Int): Int {
        require(bitCount <= 8) { "Cannot extract more than 8 bits for UByte" }
        return extractBits(value, startBit, bitCount).toInt()
    }
    
    override fun extractBoolean(value: Long, bitPosition: Int): Boolean {
        require(bitPosition >= 0) { "Bit position must be non-negative" }
        require(bitPosition < 64) { "Bit position must be less than 64" }
        
        return ((value ushr bitPosition) and 1L) == 1L
    }
    
    override fun extractFloat(value: Long, startBit: Int): Float {
        val bits = extractBits(value, startBit, 32).toInt()
        return Float.fromBits(bits)
    }
    
    override fun extractDouble(value: Long, startBit: Int): Double {
        val bits = extractBits(value, startBit, 64)
        return Double.fromBits(bits)
    }
    
    override fun extractBitsAcrossWords(
        words: List<Long>,
        startWord: Int,
        startBit: Int,
        bitCount: Int
    ): Long {
        require(startWord >= 0) { "Start word must be non-negative" }
        require(startWord < words.size) { "Start word index out of bounds" }
        require(startBit >= 0) { "Start bit must be non-negative" }
        require(bitCount > 0) { "Bit count must be positive" }
        require(bitCount <= 64) { "Cannot extract more than 64 bits" }
        
        // Determine word size by examining the first word's significant bits
        // Assume all words have the same size
        val firstWordBits = if (words[startWord] == 0L) 16 else (64 - words[startWord].countLeadingZeroBits().coerceAtLeast(1))
        // Round up to common word sizes: 8, 16, 32, 64
        val wordSize = when {
            firstWordBits <= 8 -> 8
            firstWordBits <= 16 -> 16
            firstWordBits <= 32 -> 32
            else -> 64
        }
        
        var result = 0L
        var bitsExtracted = 0
        var currentWord = startWord
        var currentBit = startBit
        
        while (bitsExtracted < bitCount && currentWord < words.size) {
            val wordValue = words[currentWord]
            val bitsAvailableInWord = wordSize - currentBit
            val bitsToExtract = minOf(bitCount - bitsExtracted, bitsAvailableInWord)
            
            val extracted = extractBits(wordValue, currentBit, bitsToExtract)
            result = result or (extracted shl bitsExtracted)
            
            bitsExtracted += bitsToExtract
            currentWord++
            currentBit = 0  // Start from LSB in subsequent words
        }
        
        require(bitsExtracted == bitCount) { "Not enough bits available in provided words" }
        
        return result
    }
    
    override fun extractBitsAcrossWords(
        words: List<Long>,
        wordRange: IntRange,
        startBit: Int
    ): Long {
        require(!wordRange.isEmpty()) { "Word range must not be empty" }
        require(wordRange.first >= 0) { "Word range start must be non-negative" }
        require(wordRange.last < words.size) { "Word range end index out of bounds" }
        require(startBit >= 0) { "Start bit must be non-negative" }
        
        // Determine word size by examining the first word's significant bits.
        // Assume all words have the same size.
        val firstWordBits = if (words[wordRange.first] == 0L) {
            16
        } else {
            (64 - words[wordRange.first].countLeadingZeroBits().coerceAtLeast(1))
        }
        // Round up to common word sizes: 8, 16, 32, 64
        val wordSize = when {
            firstWordBits <= 8 -> 8
            firstWordBits <= 16 -> 16
            firstWordBits <= 32 -> 32
            else -> 64
        }
        
        val wordCount = wordRange.last - wordRange.first + 1
        val totalBitsAvailable = wordCount * wordSize - startBit
        
        require(totalBitsAvailable > 0) { "No bits available in the specified range" }
        require(totalBitsAvailable <= 64) { "Cannot extract more than 64 bits from the specified word range" }
        
        return extractBitsAcrossWords(
            words = words,
            startWord = wordRange.first,
            startBit = startBit,
            bitCount = totalBitsAvailable
        )
    }
}
