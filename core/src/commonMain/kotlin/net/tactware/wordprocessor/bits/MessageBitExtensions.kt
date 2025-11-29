package net.tactware.wordprocessor.bits

import net.tactware.wordprocessor.holder.Message

/**
 * Extension function to create a MessageBitProcessor for this message.
 */
fun Message.bitProcessor(): MessageBitProcessor {
    return MessageBitProcessor(this)
}

/**
 * Extension function to extract bits from a specific word.
 */
fun Message.extractBits(wordIndex: Int, startBit: Int, bitCount: Int): Long {
    return bitProcessor().extractBits(wordIndex, startBit, bitCount)
}

/**
 * Extension function to extract a signed integer from bits.
 */
fun Message.extractInt(wordIndex: Int, startBit: Int, bitCount: Int): Int {
    return bitProcessor().extractInt(wordIndex, startBit, bitCount)
}

/**
 * Extension function to extract an unsigned integer from bits.
 */
fun Message.extractUInt(wordIndex: Int, startBit: Int, bitCount: Int): Long {
    return bitProcessor().extractUInt(wordIndex, startBit, bitCount)
}

/**
 * Extension function to extract a signed short from bits.
 */
fun Message.extractShort(wordIndex: Int, startBit: Int, bitCount: Int): Short {
    return bitProcessor().extractShort(wordIndex, startBit, bitCount)
}

/**
 * Extension function to extract an unsigned short from bits.
 */
fun Message.extractUShort(wordIndex: Int, startBit: Int, bitCount: Int): Int {
    return bitProcessor().extractUShort(wordIndex, startBit, bitCount)
}

/**
 * Extension function to extract a signed byte from bits.
 */
fun Message.extractByte(wordIndex: Int, startBit: Int, bitCount: Int): Byte {
    return bitProcessor().extractByte(wordIndex, startBit, bitCount)
}

/**
 * Extension function to extract an unsigned byte from bits.
 */
fun Message.extractUByte(wordIndex: Int, startBit: Int, bitCount: Int): Int {
    return bitProcessor().extractUByte(wordIndex, startBit, bitCount)
}

/**
 * Extension function to extract a boolean from a single bit.
 */
fun Message.extractBoolean(wordIndex: Int, bitPosition: Int): Boolean {
    return bitProcessor().extractBoolean(wordIndex, bitPosition)
}

/**
 * Extension function to extract a float from bits.
 */
fun Message.extractFloat(wordIndex: Int, startBit: Int): Float {
    return bitProcessor().extractFloat(wordIndex, startBit)
}

/**
 * Extension function to extract a double from bits.
 */
fun Message.extractDouble(wordIndex: Int, startBit: Int): Double {
    return bitProcessor().extractDouble(wordIndex, startBit)
}

/**
 * Extension function to extract bits across multiple words.
 */
fun Message.extractBitsAcrossWords(startWord: Int, startBit: Int, bitCount: Int): Long {
    return bitProcessor().extractBitsAcrossWords(startWord, startBit, bitCount)
}

/**
 * Extension function to extract bits across an inclusive range of words.
 */
fun Message.extractBitsAcrossWords(wordRange: IntRange, startBit: Int = 0): Long {
    return bitProcessor().extractBitsAcrossWords(wordRange, startBit)
}

/**
 * Extension function to extract a fixed-length ASCII string across an
 * inclusive range of words.
 *
 * Characters are treated as 8-bit code units packed little-endian in the
 * extracted bits (first 8 bits = first character, etc.).
 */
fun Message.extractStringAcrossWords(
    wordRange: IntRange,
    startBit: Int = 0,
    length: Int
): String {
    return bitProcessor().extractStringAcrossWords(wordRange, startBit, length)
}

/**
 * Extension function to extract a bit field.
 */
fun Message.extractField(bitField: BitField): Any {
    return bitProcessor().extractField(bitField)
}

/**
 * Extension function to extract multiple bit fields.
 */
fun Message.extractFields(bitFields: Map<String, BitField>): Map<String, Any> {
    return bitProcessor().extractFields(bitFields)
}

/**
 * Extension function to extract a named bit field.
 */
fun Message.extractNamedField(fieldName: String, bitFields: Map<String, BitField>): Any {
    return bitProcessor().extractNamedField(fieldName, bitFields)
}
