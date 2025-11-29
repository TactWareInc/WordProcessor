package net.tactware.wordprocessor.holder

import net.tactware.wordprocessor.core.WordProcessor

/**
 * Extracts words from a byte array and returns them as a WordHolder.
 *
 * @param bytes The byte array to process
 * @return A WordHolder containing the extracted words
 */
fun WordProcessor.extractToHolder(bytes: ByteArray): WordHolder {
    return WordHolderFactory.createWordHolder(bytes, this)
}

/**
 * Extracts words from a byte array and returns them as a Message.
 *
 * @param bytes The byte array to process
 * @param expectedWordCount The expected number of words in the message
 * @return A Message containing the extracted words
 */
fun WordProcessor.extractToMessage(bytes: ByteArray, expectedWordCount: Int): Message {
    return WordHolderFactory.createMessage(bytes, expectedWordCount, this)
}

/**
 * Extracts words from a byte array and returns them as a Message with field definitions.
 *
 * @param bytes The byte array to process
 * @param expectedWordCount The expected number of words in the message
 * @param fieldDefinitions Map of field names to their definitions
 * @return A Message containing the extracted words with field mapping
 */
fun WordProcessor.extractToMessage(
    bytes: ByteArray,
    expectedWordCount: Int,
    fieldDefinitions: Map<String, FieldDefinition>
): Message {
    return WordHolderFactory.createMessage(bytes, expectedWordCount, this, fieldDefinitions)
}

/**
 * Creates a Message from a list of words.
 *
 * @param words The list of words
 * @param expectedWordCount The expected number of words in the message
 * @return A Message containing the provided words
 */
fun WordProcessor.createMessage(words: List<Long>, expectedWordCount: Int): Message {
    return WordHolderFactory.createMessageFromWords(words, expectedWordCount, this)
}

/**
 * Creates a Message from a list of words with field definitions.
 *
 * @param words The list of words
 * @param expectedWordCount The expected number of words in the message
 * @param fieldDefinitions Map of field names to their definitions
 * @return A Message containing the provided words with field mapping
 */
fun WordProcessor.createMessage(
    words: List<Long>,
    expectedWordCount: Int,
    fieldDefinitions: Map<String, FieldDefinition>
): Message {
    return WordHolderFactory.createMessageFromWords(words, expectedWordCount, this, fieldDefinitions)
}
