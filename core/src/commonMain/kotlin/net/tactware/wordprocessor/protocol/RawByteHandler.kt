package net.tactware.wordprocessor.protocol

/**
 * Protocol handler for raw byte data without any protocol overhead.
 * This is a pass-through handler that returns data unchanged.
 */
class RawByteHandler : ProtocolHandler {
    /**
     * Returns the raw data unchanged as there is no protocol overhead.
     *
     * @param rawData The input byte array
     * @return The same byte array
     */
    override fun extractBytes(rawData: ByteArray): ByteArray = rawData
    
    /**
     * Returns the data unchanged as there is no protocol encoding needed.
     *
     * @param data The input byte array
     * @return The same byte array
     */
    override fun encodeBytes(data: ByteArray): ByteArray = data
    
    /**
     * Always returns true as raw bytes are always valid.
     *
     * @param rawData The byte array to validate
     * @return true
     */
    override fun validate(rawData: ByteArray): Boolean = true
}
