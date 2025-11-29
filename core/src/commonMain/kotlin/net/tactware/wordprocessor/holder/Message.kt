package net.tactware.wordprocessor.holder

/**
 * Interface representing a message with a defined structure according to an ICD.
 * Extends WordHolder to provide message-specific functionality.
 */
interface Message : WordHolder {
    /**
     * The expected word count for this message type as defined in the ICD.
     */
    val expectedWordCount: Int
    
    /**
     * Validates that the message has the expected word count.
     *
     * @return true if the message has the expected word count, false otherwise
     */
    fun isValid(): Boolean
    
    /**
     * Gets a named field from the message.
     * Field definitions are specific to the message implementation.
     *
     * @param fieldName The name of the field to retrieve
     * @return The word value for the specified field
     * @throws IllegalArgumentException if the field name is not recognized
     */
    fun getField(fieldName: String): Long
    
    /**
     * Gets a named field that spans multiple words.
     *
     * @param fieldName The name of the field to retrieve
     * @return A list of word values for the specified field
     * @throws IllegalArgumentException if the field name is not recognized
     */
    fun getFieldRange(fieldName: String): List<Long>
}
