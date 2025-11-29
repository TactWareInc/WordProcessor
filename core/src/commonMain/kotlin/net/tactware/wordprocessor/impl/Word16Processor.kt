package net.tactware.wordprocessor.impl

import net.tactware.wordprocessor.core.ByteOrder
import net.tactware.wordprocessor.core.WordSize
import net.tactware.wordprocessor.protocol.ProtocolHandler
import net.tactware.wordprocessor.protocol.RawByteHandler

/**
 * Word processor implementation for 16-bit words.
 * Processes byte arrays into 16-bit word values.
 *
 * @property byteOrder The byte order configuration
 * @property protocolHandler Optional protocol handler for processing raw data
 */
class Word16Processor(
    byteOrder: ByteOrder,
    protocolHandler: ProtocolHandler = RawByteHandler()
) : BaseWordProcessor(WordSize.WORD_16, byteOrder, protocolHandler)
