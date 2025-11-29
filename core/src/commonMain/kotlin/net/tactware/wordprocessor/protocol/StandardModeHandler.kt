package net.tactware.wordprocessor.protocol

/**
 * Protocol handler for STANDARD mode transmission.
 * Handles 10-bit character format: 1 start bit (0) + 8 data bits + 1 stop bit (1).
 * 
 * The protocol expects data to be transmitted with:
 * - Start bit: 0 (LSB)
 * - 8 data bits
 * - Stop bit: 1 (MSB)
 * 
 * For byte-based transmission, this handler processes pairs of bytes where
 * each pair represents one 16-bit word of actual data.
 */
class StandardModeHandler : ProtocolHandler {
    companion object {
        private const val START_BIT_MASK = 0x01  // LSB should be 0
        private const val STOP_BIT_MASK = 0x200  // Bit 9 should be 1
        private const val DATA_MASK = 0x1FE      // Bits 1-8 contain data
        private const val DATA_SHIFT = 1
    }
    
    /**
     * Extracts 8-bit data bytes from 10-bit character format.
     * Validates start and stop bits for each character.
     *
     * @param rawData The raw data in 10-bit character format (as byte pairs)
     * @return A byte array containing only the 8-bit data
     * @throws IllegalStateException if start or stop bits are invalid
     * @throws IllegalArgumentException if raw data length is not even
     */
    override fun extractBytes(rawData: ByteArray): ByteArray {
        require(rawData.size % 2 == 0) { 
            "Raw data length must be even for 10-bit character processing" 
        }
        
        val result = mutableListOf<Byte>()
        
        // Process pairs of bytes as 10-bit characters
        for (i in rawData.indices step 2) {
            // Combine two bytes into a 10-bit value (little-endian)
            val tenBitChar = ((rawData[i].toInt() and 0xFF)) or 
                            ((rawData[i + 1].toInt() and 0xFF) shl 8)
            
            // Validate start bit (should be 0)
            if ((tenBitChar and START_BIT_MASK) != 0) {
                throw IllegalStateException(
                    "Invalid start bit at position $i: expected 0, got 1"
                )
            }
            
            // Validate stop bit (should be 1)
            if ((tenBitChar and STOP_BIT_MASK) == 0) {
                throw IllegalStateException(
                    "Invalid stop bit at position $i: expected 1, got 0"
                )
            }
            
            // Extract 8 data bits
            val dataByte = ((tenBitChar and DATA_MASK) shr DATA_SHIFT).toByte()
            result.add(dataByte)
        }
        
        return result.toByteArray()
    }
    
    /**
     * Encodes 8-bit data bytes into 10-bit character format.
     * Adds start bit (0) and stop bit (1) to each byte.
     *
     * @param data The clean 8-bit data bytes
     * @return A byte array with 10-bit character encoding (as byte pairs)
     */
    override fun encodeBytes(data: ByteArray): ByteArray {
        val result = mutableListOf<Byte>()
        
        for (byte in data) {
            // Create 10-bit character: start bit (0) + 8 data bits + stop bit (1)
            val dataBits = (byte.toInt() and 0xFF) shl DATA_SHIFT
            val tenBitChar = dataBits or STOP_BIT_MASK
            
            // Split into two bytes (little-endian)
            result.add((tenBitChar and 0xFF).toByte())
            result.add(((tenBitChar shr 8) and 0xFF).toByte())
        }
        
        return result.toByteArray()
    }
    
    /**
     * Validates if the raw data conforms to 10-bit character protocol.
     * Checks that all start and stop bits are correct.
     *
     * @param rawData The raw data to validate
     * @return true if all characters have valid start and stop bits, false otherwise
     */
    override fun validate(rawData: ByteArray): Boolean {
        if (rawData.size % 2 != 0) return false
        
        for (i in rawData.indices step 2) {
            val tenBitChar = ((rawData[i].toInt() and 0xFF)) or 
                            ((rawData[i + 1].toInt() and 0xFF) shl 8)
            
            // Check start bit is 0
            if ((tenBitChar and START_BIT_MASK) != 0) return false
            
            // Check stop bit is 1
            if ((tenBitChar and STOP_BIT_MASK) == 0) return false
        }
        
        return true
    }
}
