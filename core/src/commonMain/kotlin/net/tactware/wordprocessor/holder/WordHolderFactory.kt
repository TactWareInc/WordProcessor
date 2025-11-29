package net.tactware.wordprocessor.holder

import net.tactware.wordprocessor.core.WordProcessor

/**
 * Factory for creating WordHolder and Message instances from byte arrays.
 */
object WordHolderFactory {
    /**
     * Creates a BasicWordHolder from a byte array.
     *
     * @param bytes The byte array to process
     * @param processor The word processor to use for extraction
     * @return A BasicWordHolder containing the extracted words
     */
    fun createWordHolder(bytes: ByteArray, processor: WordProcessor): WordHolder {
        val words = processor.extractWords(bytes)
        return BasicWordHolder(words, processor)
    }
    
    /**
     * Creates a BasicMessage from a byte array with expected word count validation.
     *
     * @param bytes The byte array to process
     * @param expectedWordCount The expected number of words in the message
     * @param processor The word processor to use for extraction
     * @return A BasicMessage containing the extracted words
     */
    fun createMessage(
        bytes: ByteArray,
        expectedWordCount: Int,
        processor: WordProcessor
    ): Message {
        val words = processor.extractWords(bytes)
        return BasicMessage(words, expectedWordCount, processor)
    }
    
    /**
     * Creates a BasicMessage from a byte array with field definitions.
     *
     * @param bytes The byte array to process
     * @param expectedWordCount The expected number of words in the message
     * @param processor The word processor to use for extraction
     * @param fieldDefinitions Map of field names to their definitions
     * @return A BasicMessage containing the extracted words with field mapping
     */
    fun createMessage(
        bytes: ByteArray,
        expectedWordCount: Int,
        processor: WordProcessor,
        fieldDefinitions: Map<String, FieldDefinition>
    ): Message {
        val words = processor.extractWords(bytes)
        return BasicMessage(words, expectedWordCount, processor, fieldDefinitions)
    }
    
    /**
     * Creates a BasicMessage from a list of words.
     * Useful when words have already been extracted.
     *
     * @param words The list of words
     * @param expectedWordCount The expected number of words in the message
     * @param processor The word processor to use for byte array conversion
     * @return A BasicMessage containing the provided words
     */
    fun createMessageFromWords(
        words: List<Long>,
        expectedWordCount: Int,
        processor: WordProcessor
    ): Message {
        return BasicMessage(words, expectedWordCount, processor)
    }
    
    /**
     * Creates a BasicMessage from a list of words with field definitions.
     *
     * @param words The list of words
     * @param expectedWordCount The expected number of words in the message
     * @param processor The word processor to use for byte array conversion
     * @param fieldDefinitions Map of field names to their definitions
     * @return A BasicMessage containing the provided words with field mapping
     */
    fun createMessageFromWords(
        words: List<Long>,
        expectedWordCount: Int,
        processor: WordProcessor,
        fieldDefinitions: Map<String, FieldDefinition>
    ): Message {
        return BasicMessage(words, expectedWordCount, processor, fieldDefinitions)
    }
}
