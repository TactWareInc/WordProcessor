package net.tactware.wordprocessor.impl

import net.tactware.wordprocessor.core.ByteOrder
import net.tactware.wordprocessor.core.WordProcessorFactory
import net.tactware.wordprocessor.core.WordSize
import net.tactware.wordprocessor.protocol.StandardModeHandler
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class WordProcessorTest {

    @Test
    fun `test 16-bit word extraction with big-endian`() {
        val processor = WordProcessorFactory.create(WordSize.WORD_16, ByteOrder.BIG_ENDIAN)
        val bytes = byteArrayOf(0x12, 0x34, 0x56, 0x78)

        val words = processor.extractWords(bytes)

        assertEquals(2, words.size)
        assertEquals(0x1234L, words[0])
        assertEquals(0x5678L, words[1])
    }

    @Test
    fun `test 16-bit word extraction with little-endian`() {
        val processor = WordProcessorFactory.create(WordSize.WORD_16, ByteOrder.LITTLE_ENDIAN)
        val bytes = byteArrayOf(0x12, 0x34, 0x56, 0x78)

        val words = processor.extractWords(bytes)

        assertEquals(2, words.size)
        assertEquals(0x3412L, words[0])
        assertEquals(0x7856L, words[1])
    }

    @Test
    fun `test 32-bit word extraction with big-endian`() {
        val processor = WordProcessorFactory.create(WordSize.WORD_32, ByteOrder.BIG_ENDIAN)
        val bytes = byteArrayOf(0x12, 0x34, 0x56, 0x78, 0x9A.toByte(), 0xBC.toByte(), 0xDE.toByte(), 0xF0.toByte())

        val words = processor.extractWords(bytes)

        assertEquals(2, words.size)
        assertEquals(0x12345678L, words[0])
        assertEquals(0x9ABCDEF0uL.toLong(), words[1])
    }

    @Test
    fun `test 32-bit word extraction with little-endian`() {
        val processor = WordProcessorFactory.create(WordSize.WORD_32, ByteOrder.LITTLE_ENDIAN)
        val bytes = byteArrayOf(0x12, 0x34, 0x56, 0x78, 0x9A.toByte(), 0xBC.toByte(), 0xDE.toByte(), 0xF0.toByte())

        val words = processor.extractWords(bytes)

        assertEquals(2, words.size)
        assertEquals(0x78563412L, words[0])
        assertEquals(0xF0DEBC9AuL.toLong(), words[1])
    }

    @Test
    fun `test 64-bit word extraction with big-endian`() {
        val processor = WordProcessorFactory.create(WordSize.WORD_64, ByteOrder.BIG_ENDIAN)
        val bytes = byteArrayOf(
            0x01, 0x23, 0x45, 0x67, 0x89.toByte(), 0xAB.toByte(), 0xCD.toByte(), 0xEF.toByte()
        )

        val words = processor.extractWords(bytes)

        assertEquals(1, words.size)
        assertEquals(0x0123456789ABCDEFL, words[0])
    }

    @Test
    fun `test 64-bit word extraction with little-endian`() {
        val processor = WordProcessorFactory.create(WordSize.WORD_64, ByteOrder.LITTLE_ENDIAN)
        val bytes = byteArrayOf(
            0x01, 0x23, 0x45, 0x67, 0x89.toByte(), 0xAB.toByte(), 0xCD.toByte(), 0xEF.toByte()
        )

        val words = processor.extractWords(bytes)

        assertEquals(1, words.size)
        assertEquals(0xEFCDAB8967452301uL.toLong(), words[0])
    }

    @Test
    fun `test word to bytes conversion big-endian`() {
        val processor = WordProcessorFactory.create(WordSize.WORD_16, ByteOrder.BIG_ENDIAN)

        val bytes = processor.wordToBytes(0x1234L)

        assertEquals(2, bytes.size)
        assertEquals(0x12.toByte(), bytes[0])
        assertEquals(0x34.toByte(), bytes[1])
    }

    @Test
    fun `test word to bytes conversion little-endian`() {
        val processor = WordProcessorFactory.create(WordSize.WORD_16, ByteOrder.LITTLE_ENDIAN)

        val bytes = processor.wordToBytes(0x1234L)

        assertEquals(2, bytes.size)
        assertEquals(0x34.toByte(), bytes[0])
        assertEquals(0x12.toByte(), bytes[1])
    }

    @Test
    fun `test unaligned byte array throws exception`() {
        val processor = WordProcessorFactory.create(WordSize.WORD_16, ByteOrder.BIG_ENDIAN)
        val bytes = byteArrayOf(0x12, 0x34, 0x56) // 3 bytes, not aligned to 16-bit

        assertFailsWith<IllegalArgumentException> {

            processor.extractWords(bytes)
        }
    }

    @Test
    fun `test validation with aligned bytes`() {
        val processor = WordProcessorFactory.create(WordSize.WORD_16, ByteOrder.BIG_ENDIAN)
        val bytes = byteArrayOf(0x12, 0x34, 0x56, 0x78)

        assertTrue(processor.validate(bytes))
    }

    @Test
    fun `test validation with unaligned bytes`() {
        val processor = WordProcessorFactory.create(WordSize.WORD_16, ByteOrder.BIG_ENDIAN)
        val bytes = byteArrayOf(0x12, 0x34, 0x56)

        assertFalse(processor.validate(bytes))
    }

    @Test
    fun `test bytes to word at offset`() {
        val processor = WordProcessorFactory.create(WordSize.WORD_16, ByteOrder.BIG_ENDIAN)
        val bytes = byteArrayOf(0x12, 0x34, 0x56, 0x78, 0x9A.toByte(), 0xBC.toByte())

        assertEquals(0x1234L, processor.bytesToWord(bytes, 0))
        assertEquals(0x5678L, processor.bytesToWord(bytes, 2))
        assertEquals(0x9ABCL, processor.bytesToWord(bytes, 4))
    }

    @Test
    fun `test standard mode protocol handler extraction`() {
        val handler = StandardModeHandler()

        // Create 10-bit character: start(0) + data(0x41='A') + stop(1)
        // 0x41 << 1 = 0x82, then OR with stop bit (0x200) = 0x282
        val tenBitChar = 0x282
        val rawData = byteArrayOf(
            (tenBitChar and 0xFF).toByte(),
            ((tenBitChar shr 8) and 0xFF).toByte()
        )

        val extracted = handler.extractBytes(rawData)

        assertEquals(1, extracted.size)
        assertEquals(0x41.toByte(), extracted[0])
    }

    @Test
    fun `test standard mode protocol handler encoding`() {
        val handler = StandardModeHandler()
        val data = byteArrayOf(0x41) // 'A'

        val encoded = handler.encodeBytes(data)

        assertEquals(2, encoded.size)

        // Reconstruct 10-bit character
        val tenBitChar = ((encoded[0].toInt() and 0xFF)) or
                ((encoded[1].toInt() and 0xFF) shl 8)

        // Verify start bit is 0
        assertEquals(0, tenBitChar and 0x01)

        // Verify stop bit is 1
        assertEquals(0x200, tenBitChar and 0x200)

        // Verify data bits
        assertEquals(0x41, (tenBitChar and 0x1FE) shr 1)
    }

    @Test
    fun `test standard mode with invalid start bit`() {
        val handler = StandardModeHandler()

        // Create invalid 10-bit character with start bit = 1
        val invalidChar = 0x283 // start bit is 1
        val rawData = byteArrayOf(
            (invalidChar and 0xFF).toByte(),
            ((invalidChar shr 8) and 0xFF).toByte()
        )

        assertFailsWith<IllegalStateException> {

            handler.extractBytes(rawData)
        }
    }

    @Test
    fun `test standard mode with invalid stop bit`() {
        val handler = StandardModeHandler()

        // Create invalid 10-bit character with stop bit = 0
        val invalidChar = 0x82 // stop bit is 0
        val rawData = byteArrayOf(
            (invalidChar and 0xFF).toByte(),
            ((invalidChar shr 8) and 0xFF).toByte()
        )

        assertFailsWith<IllegalStateException> {
            handler.extractBytes(rawData)
        }
    }

    @Test
    fun `test 16-bit word processor with standard mode protocol`() {
        val handler = StandardModeHandler()
        val processor = WordProcessorFactory.create(WordSize.WORD_16, ByteOrder.BIG_ENDIAN, handler)

        // Create two bytes of data: 0x12, 0x34
        val data = byteArrayOf(0x12, 0x34)
        val encodedData = handler.encodeBytes(data)

        val words = processor.extractWords(encodedData)

        assertEquals(1, words.size)
        assertEquals(0x1234L, words[0])
    }
}
