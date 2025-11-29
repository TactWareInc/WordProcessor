package net.tactware.wordprocessor.impl

import net.tactware.wordprocessor.core.ByteOrder
import net.tactware.wordprocessor.core.WordProcessor
import net.tactware.wordprocessor.core.WordSize
import net.tactware.wordprocessor.protocol.ProtocolHandler
import net.tactware.wordprocessor.protocol.RawByteHandler

/**
 * Abstract base implementation of WordProcessor providing common functionality.
 * Subclasses implement specific word size handling.
 *
 * @property wordSize The word size configuration
 * @property byteOrder The byte order configuration
 * @property protocolHandler Optional protocol handler for processing raw data
 */
abstract class BaseWordProcessor(
    override val wordSize: WordSize,
    override val byteOrder: ByteOrder,
    protected val protocolHandler: ProtocolHandler = RawByteHandler()
) : WordProcessor {
    
    /**
     * Extracts words from a byte array after protocol processing.
     *
     * @param bytes The byte array to process
     * @return A list of words represented as Long values
     * @throws IllegalArgumentException if the byte array length is not aligned to the word size
     */
    override fun extractWords(bytes: ByteArray): List<Long> {
        // Process through protocol handler first
        val cleanBytes = protocolHandler.extractBytes(bytes)
        
        require(wordSize.isAligned(cleanBytes.size)) {
            "Byte array length ${cleanBytes.size} is not aligned to word size ${wordSize.bytes}"
        }
        
        val words = mutableListOf<Long>()
        val wordCount = wordSize.wordCount(cleanBytes.size)
        
        for (i in 0 until wordCount) {
            val offset = i * wordSize.bytes
            words.add(bytesToWord(cleanBytes, offset))
        }
        
        return words
    }
    
    /**
     * Converts bytes at a specific offset to a single word.
     * Handles byte ordering according to configuration.
     *
     * @param bytes The byte array containing the word data
     * @param offset The starting position in the byte array
     * @return The word value as a Long
     * @throws IndexOutOfBoundsException if there are insufficient bytes at the offset
     */
    override fun bytesToWord(bytes: ByteArray, offset: Int): Long {
        require(offset + wordSize.bytes <= bytes.size) {
            "Insufficient bytes at offset $offset for word size ${wordSize.bytes}"
        }
        
        var result = 0L
        
        when (byteOrder) {
            ByteOrder.BIG_ENDIAN -> {
                // Most significant byte first
                for (i in 0 until wordSize.bytes) {
                    result = (result shl 8) or (bytes[offset + i].toInt() and 0xFF).toLong()
                }
            }
            ByteOrder.LITTLE_ENDIAN -> {
                // Least significant byte first
                for (i in wordSize.bytes - 1 downTo 0) {
                    result = (result shl 8) or (bytes[offset + i].toInt() and 0xFF).toLong()
                }
            }
        }
        
        return result
    }
    
    /**
     * Converts a word value to its byte representation.
     * Handles byte ordering according to configuration.
     *
     * @param word The word value to convert
     * @return A byte array representing the word
     */
    override fun wordToBytes(word: Long): ByteArray {
        val bytes = ByteArray(wordSize.bytes)
        var value = word
        
        when (byteOrder) {
            ByteOrder.BIG_ENDIAN -> {
                // Most significant byte first
                for (i in wordSize.bytes - 1 downTo 0) {
                    bytes[i] = (value and 0xFF).toByte()
                    value = value shr 8
                }
            }
            ByteOrder.LITTLE_ENDIAN -> {
                // Least significant byte first
                for (i in 0 until wordSize.bytes) {
                    bytes[i] = (value and 0xFF).toByte()
                    value = value shr 8
                }
            }
        }
        
        return bytes
    }
    
    /**
     * Validates if a byte array can be processed by this word processor.
     * Checks protocol validity and word alignment.
     *
     * @param bytes The byte array to validate
     * @return true if the byte array is valid for processing, false otherwise
     */
    override fun validate(bytes: ByteArray): Boolean {
        if (!protocolHandler.validate(bytes)) return false
        
        val cleanBytes = try {
            protocolHandler.extractBytes(bytes)
        } catch (e: Exception) {
            return false
        }
        
        return wordSize.isAligned(cleanBytes.size)
    }
}
