package net.tactware.wordprocessor.bits

/**
 * Enumeration of supported bit field data types.
 */
enum class BitFieldType {
    /** Signed integer (up to 32 bits) */
    INT,
    
    /** Unsigned integer (up to 32 bits) */
    UINT,
    
    /** Signed short (up to 16 bits) */
    SHORT,
    
    /** Unsigned short (up to 16 bits) */
    USHORT,
    
    /** Signed byte (up to 8 bits) */
    BYTE,
    
    /** Unsigned byte (up to 8 bits) */
    UBYTE,
    
    /** Boolean (1 bit) */
    BOOLEAN,
    
    /** 32-bit IEEE 754 float */
    FLOAT,
    
    /** 64-bit IEEE 754 double */
    DOUBLE,
    
    /** Raw bits (no conversion) */
    BITS
}
