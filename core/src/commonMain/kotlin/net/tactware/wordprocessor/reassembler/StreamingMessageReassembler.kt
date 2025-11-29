package net.tactware.wordprocessor.reassembler

import net.tactware.wordprocessor.core.WordProcessor
import net.tactware.wordprocessor.holder.FieldDefinition
import net.tactware.wordprocessor.holder.Message

/**
 * Streaming message reassembler that emits messages via callbacks.
 * Useful for event-driven or reactive programming patterns.
 *
 * @property expectedWordCount The expected number of words in each message
 * @property processor The word processor to use for message extraction
 * @property fieldDefinitions Optional field definitions for the messages
 * @property onMessage Callback invoked when a complete message is assembled
 */
class StreamingMessageReassembler(
    expectedWordCount: Int,
    processor: WordProcessor,
    fieldDefinitions: Map<String, FieldDefinition> = emptyMap(),
    private val onMessage: (Message) -> Unit
) : MessageReassembler by BasicMessageReassembler(expectedWordCount, processor, fieldDefinitions) {
    
    private val delegate = BasicMessageReassembler(expectedWordCount, processor, fieldDefinitions)
    
    override val expectedWordCount: Int = delegate.expectedWordCount
    override val bufferedByteCount: Int get() = delegate.bufferedByteCount
    override val expectedByteCount: Int = delegate.expectedByteCount
    
    /**
     * Feeds a chunk of bytes into the reassembler.
     * Automatically invokes the onMessage callback for each complete message.
     *
     * @param chunk The byte chunk to process
     * @return A list of complete messages extracted from the buffer
     */
    override fun feed(chunk: ByteArray): List<Message> {
        val messages = delegate.feed(chunk)
        messages.forEach(onMessage)
        return messages
    }
    
    /**
     * Feeds a single byte into the reassembler.
     * Automatically invokes the onMessage callback if a complete message is formed.
     *
     * @param byte The byte to process
     * @return A list of complete messages (usually empty or single message)
     */
    override fun feedByte(byte: Byte): List<Message> {
        val messages = delegate.feedByte(byte)
        messages.forEach(onMessage)
        return messages
    }
    
    override fun peek(): Message? = delegate.peek()
    
    override fun poll(): Message? = delegate.poll()
    
    override fun reset() = delegate.reset()
    
    override fun getBuffer(): ByteArray = delegate.getBuffer()
    
    override fun toString(): String {
        return "StreamingMessageReassembler(delegate=$delegate)"
    }
}


