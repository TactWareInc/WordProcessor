package net.tactware.wordprocessor.holder

import net.tactware.wordprocessor.core.ByteOrder
import net.tactware.wordprocessor.core.WordProcessorFactory
import net.tactware.wordprocessor.core.WordSize
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class WordHolderTest {
    
    @Test
    fun `test basic word holder creation from bytes`() {
        val processor = WordProcessorFactory.create(WordSize.WORD_16, ByteOrder.BIG_ENDIAN)
        val bytes = byteArrayOf(0x12, 0x34, 0x56, 0x78, 0x9A.toByte(), 0xBC.toByte())
        
        val holder = WordHolderFactory.createWordHolder(bytes, processor)
        
        assertEquals(3, holder.wordCount)
        assertEquals(0x1234L, holder.getWord(0))
        assertEquals(0x5678L, holder.getWord(1))
        assertEquals(0x9ABCL, holder.getWord(2))
    }
    
    @Test
    fun `test word holder get word range 0-based`() {
        val processor = WordProcessorFactory.create(WordSize.WORD_16, ByteOrder.BIG_ENDIAN)
        val words = listOf(0x1111L, 0x2222L, 0x3333L, 0x4444L, 0x5555L, 0x6666L)
        val holder = BasicWordHolder(words, processor)
        
        val range = holder.getWordRange(1, 3)
        
        assertEquals(3, range.size)
        assertEquals(0x2222L, range[0])
        assertEquals(0x3333L, range[1])
        assertEquals(0x4444L, range[2])
    }
    
    @Test
    fun `test word holder get words 1-based indexing`() {
        val processor = WordProcessorFactory.create(WordSize.WORD_16, ByteOrder.BIG_ENDIAN)
        val words = listOf(0x1111L, 0x2222L, 0x3333L, 0x4444L, 0x5555L, 0x6666L, 0x7777L, 0x8888L)
        val holder = BasicWordHolder(words, processor)
        
        // Get words 5-8 (1-based) which are indices 4-7 (0-based)
        val range = holder.getWords1Based(5, 8)
        
        assertEquals(4, range.size)
        assertEquals(0x5555L, range[0])
        assertEquals(0x6666L, range[1])
        assertEquals(0x7777L, range[2])
        assertEquals(0x8888L, range[3])
    }
    
    @Test
    fun `test word holder to byte array conversion`() {
        val processor = WordProcessorFactory.create(WordSize.WORD_16, ByteOrder.BIG_ENDIAN)
        val originalBytes = byteArrayOf(0x12, 0x34, 0x56, 0x78)
        val holder = WordHolderFactory.createWordHolder(originalBytes, processor)
        
        val convertedBytes = holder.toByteArray()
        
        assertTrue(originalBytes.contentEquals(convertedBytes))
    }
    
    @Test
    fun `test word holder invalid index throws exception`() {
        val processor = WordProcessorFactory.create(WordSize.WORD_16, ByteOrder.BIG_ENDIAN)
        val words = listOf(0x1111L, 0x2222L, 0x3333L)
        val holder = BasicWordHolder(words, processor)
        
        assertFailsWith<IllegalArgumentException> {
            holder.getWord(5)
        }
    }
    
    @Test
    fun `test word holder invalid range throws exception`() {
        val processor = WordProcessorFactory.create(WordSize.WORD_16, ByteOrder.BIG_ENDIAN)
        val words = listOf(0x1111L, 0x2222L, 0x3333L)
        val holder = BasicWordHolder(words, processor)

        assertFailsWith<IllegalArgumentException> {
            holder.getWordRange(2, 1) // start > end
        }
    }
    
    @Test
    fun `test 1-based indexing with invalid word number throws exception`() {
        val processor = WordProcessorFactory.create(WordSize.WORD_16, ByteOrder.BIG_ENDIAN)
        val words = listOf(0x1111L, 0x2222L, 0x3333L)
        val holder = BasicWordHolder(words, processor)

        assertFailsWith<IllegalArgumentException> {
            holder.getWords1Based(0, 2) // word 0 doesn't exist (1-based)
        }

        assertFailsWith<IllegalArgumentException> {
            holder.getWords1Based(1, 5) // word 5 out of bounds
        }
    }
    
    @Test
    fun `test message creation with expected word count`() {
        val processor = WordProcessorFactory.create(WordSize.WORD_16, ByteOrder.BIG_ENDIAN)
        val bytes = byteArrayOf(
            0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 0x08,
            0x09, 0x0A, 0x0B, 0x0C, 0x0D, 0x0E, 0x0F, 0x10
        )
        
        val message = WordHolderFactory.createMessage(bytes, 8, processor)
        
        assertEquals(8, message.wordCount)
        assertEquals(8, message.expectedWordCount)
        assertTrue(message.isValid())
    }
    
    @Test
    fun `test message with incorrect word count is invalid`() {
        val processor = WordProcessorFactory.create(WordSize.WORD_16, ByteOrder.BIG_ENDIAN)
        val bytes = byteArrayOf(0x01, 0x02, 0x03, 0x04, 0x05, 0x06)
        
        val message = WordHolderFactory.createMessage(bytes, 5, processor) // Expected 5, got 3
        
        assertEquals(3, message.wordCount)
        assertEquals(5, message.expectedWordCount)
        assertFalse(message.isValid())
    }
    
    @Test
    fun `test message with field definitions`() {
        val processor = WordProcessorFactory.create(WordSize.WORD_16, ByteOrder.BIG_ENDIAN)
        val words = listOf(0x1111L, 0x2222L, 0x3333L, 0x4444L, 0x5555L)
        
        val fieldDefinitions = mapOf(
            "header" to FieldDefinition("header", 0, 0),
            "payload" to FieldDefinition("payload", 1, 3),
            "checksum" to FieldDefinition("checksum", 4, 4)
        )
        
        val message = WordHolderFactory.createMessageFromWords(words, 5, processor, fieldDefinitions)
        
        assertEquals(0x1111L, message.getField("header"))
        assertEquals(0x5555L, message.getField("checksum"))
        
        val payload = message.getFieldRange("payload")
        assertEquals(3, payload.size)
        assertEquals(0x2222L, payload[0])
        assertEquals(0x3333L, payload[1])
        assertEquals(0x4444L, payload[2])
    }
    
    @Test
    fun `test message get single word field using getField`() {
        val processor = WordProcessorFactory.create(WordSize.WORD_16, ByteOrder.BIG_ENDIAN)
        val words = listOf(0xAAAAL, 0xBBBBL, 0xCCCCL)
        
        val fieldDefinitions = mapOf(
            "id" to FieldDefinition("id", 0, 0),
            "type" to FieldDefinition("type", 1, 1)
        )
        
        val message = WordHolderFactory.createMessageFromWords(words, 3, processor, fieldDefinitions)
        
        assertEquals(0xAAAAL, message.getField("id"))
        assertEquals(0xBBBBL, message.getField("type"))
    }
    
    @Test
    fun `test message getField throws exception for multi-word field`() {
        val processor = WordProcessorFactory.create(WordSize.WORD_16, ByteOrder.BIG_ENDIAN)
        val words = listOf(0x1111L, 0x2222L, 0x3333L)
        
        val fieldDefinitions = mapOf(
            "data" to FieldDefinition("data", 0, 2) // Multi-word field
        )
        
        val message = WordHolderFactory.createMessageFromWords(words, 3, processor, fieldDefinitions)

        assertFailsWith<IllegalArgumentException> {
            message.getField("data") // Should use getFieldRange instead
        }
    }
    
    @Test
    fun `test message with unknown field throws exception`() {
        val processor = WordProcessorFactory.create(WordSize.WORD_16, ByteOrder.BIG_ENDIAN)
        val words = listOf(0x1111L, 0x2222L)
        
        val fieldDefinitions = mapOf(
            "header" to FieldDefinition("header", 0, 0)
        )
        
        val message = WordHolderFactory.createMessageFromWords(words, 2, processor, fieldDefinitions)

        assertFailsWith<IllegalArgumentException> {
            message.getField("unknown")
        }
    }
    
    @Test
    fun `test word processor extension extractToHolder`() {
        val processor = WordProcessorFactory.create(WordSize.WORD_16, ByteOrder.BIG_ENDIAN)
        val bytes = byteArrayOf(0x12, 0x34, 0x56, 0x78)
        
        val holder = processor.extractToHolder(bytes)
        
        assertEquals(2, holder.wordCount)
        assertEquals(0x1234L, holder.getWord(0))
        assertEquals(0x5678L, holder.getWord(1))
    }
    
    @Test
    fun `test word processor extension extractToMessage`() {
        val processor = WordProcessorFactory.create(WordSize.WORD_16, ByteOrder.BIG_ENDIAN)
        val bytes = byteArrayOf(0x01, 0x02, 0x03, 0x04, 0x05, 0x06)
        
        val message = processor.extractToMessage(bytes, 3)
        
        assertEquals(3, message.wordCount)
        assertTrue(message.isValid())
    }
    
    @Test
    fun `test ICD scenario - 24 word message get words 5-8`() {
        val processor = WordProcessorFactory.create(WordSize.WORD_16, ByteOrder.BIG_ENDIAN)
        
        // Create a 24-word message (48 bytes)
        val bytes = ByteArray(48) { it.toByte() }
        val message = processor.extractToMessage(bytes, 24)
        
        assertTrue(message.isValid())
        assertEquals(24, message.wordCount)
        
        // Get words 5-8 using 1-based indexing (as in ICD)
        val words5to8 = message.getWords1Based(5, 8)
        
        assertEquals(4, words5to8.size)
        // Word 5 is at index 4, which is bytes 8-9
        assertEquals(0x0809L, words5to8[0])
        // Word 6 is at index 5, which is bytes 10-11
        assertEquals(0x0A0BL, words5to8[1])
        // Word 7 is at index 6, which is bytes 12-13
        assertEquals(0x0C0DL, words5to8[2])
        // Word 8 is at index 7, which is bytes 14-15
        assertEquals(0x0E0FL, words5to8[3])
    }
    
    @Test
    fun `test ICD scenario with field definitions`() {
        val processor = WordProcessorFactory.create(WordSize.WORD_16, ByteOrder.BIG_ENDIAN)
        
        // Define a message structure according to an ICD
        val fieldDefinitions = mapOf(
            "message_id" to FieldDefinition("message_id", 0, 0),      // Word 1
            "timestamp" to FieldDefinition("timestamp", 1, 2),         // Words 2-3
            "data_block_1" to FieldDefinition("data_block_1", 3, 6),  // Words 4-7
            "data_block_2" to FieldDefinition("data_block_2", 7, 10), // Words 8-11
            "status" to FieldDefinition("status", 11, 11),             // Word 12
            "payload" to FieldDefinition("payload", 12, 22),           // Words 13-23
            "checksum" to FieldDefinition("checksum", 23, 23)          // Word 24
        )
        
        // Create a 24-word message
        val bytes = ByteArray(48) { (it + 1).toByte() }
        val message = processor.extractToMessage(bytes, 24, fieldDefinitions)
        
        assertTrue(message.isValid())
        
        // Access fields by name
        assertEquals(0x0102L, message.getField("message_id"))
        assertEquals(0x1718L, message.getField("status"))
        assertEquals(0x2F30L, message.getField("checksum"))
        
        // Access multi-word fields
        val timestamp = message.getFieldRange("timestamp")
        assertEquals(2, timestamp.size)
        assertEquals(0x0304L, timestamp[0])
        assertEquals(0x0506L, timestamp[1])
        
        val dataBlock1 = message.getFieldRange("data_block_1")
        assertEquals(4, dataBlock1.size)
        
        val payload = message.getFieldRange("payload")
        assertEquals(11, payload.size)
    }
}
