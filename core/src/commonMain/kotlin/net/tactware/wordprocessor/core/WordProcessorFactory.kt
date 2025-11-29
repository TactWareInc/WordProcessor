package net.tactware.wordprocessor.core

import net.tactware.wordprocessor.impl.Word16Processor
import net.tactware.wordprocessor.impl.Word32Processor
import net.tactware.wordprocessor.impl.Word64Processor
import net.tactware.wordprocessor.protocol.ProtocolHandler
import net.tactware.wordprocessor.protocol.RawByteHandler

/**
 * Factory for creating WordProcessor instances with specific configurations.
 */
object WordProcessorFactory {
    /**
     * Creates a word processor with the specified word size and byte order.
     * Uses raw byte processing without protocol overhead.
     *
     * @param wordSize The desired word size
     * @param byteOrder The desired byte order
     * @return A configured WordProcessor instance
     */
    fun create(wordSize: WordSize, byteOrder: ByteOrder): WordProcessor {
        return create(wordSize, byteOrder, RawByteHandler())
    }
    
    /**
     * Creates a word processor with the specified word size, byte order, and protocol handler.
     *
     * @param wordSize The desired word size
     * @param byteOrder The desired byte order
     * @param protocolHandler The protocol handler for processing raw data
     * @return A configured WordProcessor instance
     */
    fun create(
        wordSize: WordSize,
        byteOrder: ByteOrder,
        protocolHandler: ProtocolHandler
    ): WordProcessor {
        return when (wordSize) {
            WordSize.WORD_16 -> Word16Processor(byteOrder, protocolHandler)
            WordSize.WORD_32 -> Word32Processor(byteOrder, protocolHandler)
            WordSize.WORD_64 -> Word64Processor(byteOrder, protocolHandler)
        }
    }
}
