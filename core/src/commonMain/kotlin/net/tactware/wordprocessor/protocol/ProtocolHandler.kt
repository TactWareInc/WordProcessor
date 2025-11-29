package net.tactware.wordprocessor.protocol

/**
 * Interface for handling different transmission protocols.
 * Protocol handlers process raw data and extract clean byte streams.
 */
interface ProtocolHandler {
    /**
     * Processes raw protocol data and extracts clean bytes.
     *
     * @param rawData The raw data including protocol overhead
     * @return A byte array containing only the data bytes
     * @throws IllegalStateException if protocol validation fails
     */
    fun extractBytes(rawData: ByteArray): ByteArray
    
    /**
     * Encodes clean bytes into protocol format.
     *
     * @param data The clean data bytes to encode
     * @return A byte array with protocol overhead added
     */
    fun encodeBytes(data: ByteArray): ByteArray
    
    /**
     * Validates if the raw data conforms to the protocol.
     *
     * @param rawData The raw data to validate
     * @return true if the data is valid according to the protocol, false otherwise
     */
    fun validate(rawData: ByteArray): Boolean
}
