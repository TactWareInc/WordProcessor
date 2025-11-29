package net.tactware.wordprocessor.reassembler

import net.tactware.wordprocessor.core.WordProcessor
import net.tactware.wordprocessor.holder.FieldDefinition
import net.tactware.wordprocessor.holder.Message
import net.tactware.wordprocessor.holder.extractToMessage
import java.io.ByteArrayOutputStream

/**
 * Basic implementation of MessageReassembler that buffers incoming bytes
 * and emits complete messages when enough data is available.
 *
 * @property expectedWordCount The expected number of words in each message
 * @property processor The word processor to use for message extraction
 * @property fieldDefinitions Optional field definitions for the messages
 */
class BasicMessageReassembler(
    override val expectedWordCount: Int,
    private val processor: WordProcessor,
    private val fieldDefinitions: Map<String, FieldDefinition> = emptyMap()
) : MessageReassembler {
    
    private val buffer = ByteArrayOutputStream()
    
    override val expectedByteCount: Int = expectedWordCount * processor.wordSize.bytes
    
    override val bufferedByteCount: Int
        get() = buffer.size()
    
    /**
     * Feeds a chunk of bytes into the reassembler.
     * Extracts and returns all complete messages that can be formed.
     *
     * @param chunk The byte chunk to process
     * @return A list of complete messages extracted from the buffer
     */
    override fun feed(chunk: ByteArray): List<Message> {
        // Add chunk to buffer
        buffer.write(chunk)
        
        // Extract all complete messages
        return extractCompleteMessages()
    }
    
    /**
     * Feeds a single byte into the reassembler.
     *
     * @param byte The byte to process
     * @return A list of complete messages (usually empty or single message)
     */
    override fun feedByte(byte: Byte): List<Message> {
        buffer.write(byte.toInt())
        return extractCompleteMessages()
    }
    
    /**
     * Gets the next complete message if available, without removing it from the buffer.
     *
     * @return The next complete message, or null if no complete message is available
     */
    override fun peek(): Message? {
        if (!hasCompleteMessage) return null
        
        val bufferBytes = buffer.toByteArray()
        val messageBytes = bufferBytes.copyOfRange(0, expectedByteCount)
        
        return if (fieldDefinitions.isEmpty()) {
            processor.extractToMessage(messageBytes, expectedWordCount)
        } else {
            processor.extractToMessage(messageBytes, expectedWordCount, fieldDefinitions)
        }
    }
    
    /**
     * Gets and removes the next complete message if available.
     *
     * @return The next complete message, or null if no complete message is available
     */
    override fun poll(): Message? {
        if (!hasCompleteMessage) return null
        
        val bufferBytes = buffer.toByteArray()
        val messageBytes = bufferBytes.copyOfRange(0, expectedByteCount)
        val remainingBytes = bufferBytes.copyOfRange(expectedByteCount, bufferBytes.size)
        
        // Reset buffer and write remaining bytes
        buffer.reset()
        if (remainingBytes.isNotEmpty()) {
            buffer.write(remainingBytes)
        }
        
        return if (fieldDefinitions.isEmpty()) {
            processor.extractToMessage(messageBytes, expectedWordCount)
        } else {
            processor.extractToMessage(messageBytes, expectedWordCount, fieldDefinitions)
        }
    }
    
    /**
     * Clears the internal buffer, discarding any incomplete message data.
     */
    override fun reset() {
        buffer.reset()
    }
    
    /**
     * Gets the current buffer contents (for debugging/inspection).
     *
     * @return A copy of the current buffer
     */
    override fun getBuffer(): ByteArray {
        return buffer.toByteArray()
    }
    
    /**
     * Extracts all complete messages from the buffer.
     * Removes extracted messages from the buffer.
     *
     * @return A list of complete messages
     */
    private fun extractCompleteMessages(): List<Message> {
        val messages = mutableListOf<Message>()
        
        while (hasCompleteMessage) {
            poll()?.let { messages.add(it) }
        }
        
        return messages
    }
    
    override fun toString(): String {
        return "BasicMessageReassembler(expectedWords=$expectedWordCount, " +
               "expectedBytes=$expectedByteCount, buffered=$bufferedByteCount)"
    }
}
