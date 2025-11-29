package net.tactware.wordprocessor.reassembler

import net.tactware.wordprocessor.holder.Message

/**
 * Interface for reassembling messages from byte fragments.
 * Handles receiving data in bite-size pieces and emitting complete messages.
 */
interface MessageReassembler {
    /**
     * The expected word count for messages being reassembled.
     */
    val expectedWordCount: Int
    
    /**
     * The number of bytes currently buffered.
     */
    val bufferedByteCount: Int
    
    /**
     * The number of bytes needed for a complete message.
     */
    val expectedByteCount: Int
    
    /**
     * Checks if there is enough data buffered for a complete message.
     */
    val hasCompleteMessage: Boolean
        get() = bufferedByteCount >= expectedByteCount
    
    /**
     * Feeds a chunk of bytes into the reassembler.
     * May trigger emission of one or more complete messages.
     *
     * @param chunk The byte chunk to process
     * @return A list of complete messages extracted from the buffer
     */
    fun feed(chunk: ByteArray): List<Message>
    
    /**
     * Feeds a single byte into the reassembler.
     *
     * @param byte The byte to process
     * @return A list of complete messages extracted from the buffer (usually empty or single message)
     */
    fun feedByte(byte: Byte): List<Message>
    
    /**
     * Gets the next complete message if available, without removing it from the buffer.
     *
     * @return The next complete message, or null if no complete message is available
     */
    fun peek(): Message?
    
    /**
     * Gets and removes the next complete message if available.
     *
     * @return The next complete message, or null if no complete message is available
     */
    fun poll(): Message?
    
    /**
     * Clears the internal buffer, discarding any incomplete message data.
     */
    fun reset()
    
    /**
     * Gets the current buffer contents (for debugging/inspection).
     *
     * @return A copy of the current buffer
     */
    fun getBuffer(): ByteArray
}
