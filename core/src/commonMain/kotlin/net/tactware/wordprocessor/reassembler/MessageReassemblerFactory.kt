package net.tactware.wordprocessor.reassembler

import net.tactware.wordprocessor.core.WordProcessor
import net.tactware.wordprocessor.holder.FieldDefinition
import net.tactware.wordprocessor.holder.Message

/**
 * Factory for creating MessageReassembler instances.
 */
object MessageReassemblerFactory {
    /**
     * Creates a basic message reassembler.
     *
     * @param expectedWordCount The expected number of words in each message
     * @param processor The word processor to use for message extraction
     * @return A MessageReassembler instance
     */
    fun create(
        expectedWordCount: Int,
        processor: WordProcessor
    ): MessageReassembler {
        return BasicMessageReassembler(expectedWordCount, processor)
    }
    
    /**
     * Creates a message reassembler with field definitions.
     *
     * @param expectedWordCount The expected number of words in each message
     * @param processor The word processor to use for message extraction
     * @param fieldDefinitions Field definitions for the messages
     * @return A MessageReassembler instance
     */
    fun create(
        expectedWordCount: Int,
        processor: WordProcessor,
        fieldDefinitions: Map<String, FieldDefinition>
    ): MessageReassembler {
        return BasicMessageReassembler(expectedWordCount, processor, fieldDefinitions)
    }
    
    /**
     * Creates a streaming message reassembler with a callback.
     *
     * @param expectedWordCount The expected number of words in each message
     * @param processor The word processor to use for message extraction
     * @param onMessage Callback invoked when a complete message is assembled
     * @return A StreamingMessageReassembler instance
     */
    fun createStreaming(
        expectedWordCount: Int,
        processor: WordProcessor,
        onMessage: (Message) -> Unit
    ): StreamingMessageReassembler {
        return StreamingMessageReassembler(expectedWordCount, processor, emptyMap(), onMessage)
    }
    
    /**
     * Creates a streaming message reassembler with field definitions and a callback.
     *
     * @param expectedWordCount The expected number of words in each message
     * @param processor The word processor to use for message extraction
     * @param fieldDefinitions Field definitions for the messages
     * @param onMessage Callback invoked when a complete message is assembled
     * @return A StreamingMessageReassembler instance
     */
    fun createStreaming(
        expectedWordCount: Int,
        processor: WordProcessor,
        fieldDefinitions: Map<String, FieldDefinition>,
        onMessage: (Message) -> Unit
    ): StreamingMessageReassembler {
        return StreamingMessageReassembler(expectedWordCount, processor, fieldDefinitions, onMessage)
    }
}
