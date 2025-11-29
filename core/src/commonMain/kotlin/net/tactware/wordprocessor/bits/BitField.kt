package net.tactware.wordprocessor.bits

/**
 * Defines a bit field within a message word or across multiple words.
 *
 * @property name The name of the bit field
 * @property wordIndex The word index (0-based) where the field starts
 * @property startBit The starting bit position within the word (0-indexed, LSB = 0)
 * @property bitCount The number of bits in the field
 * @property type The data type of the field
 */
data class BitField(
    val name: String,
    val wordIndex: Int,
    val startBit: Int,
    val bitCount: Int,
    val type: BitFieldType
) {
    init {
        require(wordIndex >= 0) { "Word index must be non-negative" }
        require(startBit >= 0) { "Start bit must be non-negative" }
        require(bitCount > 0) { "Bit count must be positive" }
    }
}

