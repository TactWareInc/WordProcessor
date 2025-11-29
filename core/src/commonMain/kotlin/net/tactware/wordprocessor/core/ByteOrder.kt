package net.tactware.wordprocessor.core

/**
 * Defines byte ordering for multi-byte word assembly.
 */
enum class ByteOrder {
    /**
     * Big-endian byte order (most significant byte first).
     * Example: 0x1234 is stored as [0x12, 0x34]
     */
    BIG_ENDIAN,
    
    /**
     * Little-endian byte order (least significant byte first).
     * Example: 0x1234 is stored as [0x34, 0x12]
     */
    LITTLE_ENDIAN
}
