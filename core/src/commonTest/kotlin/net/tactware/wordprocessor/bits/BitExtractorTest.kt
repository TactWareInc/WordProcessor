package net.tactware.wordprocessor.bits

import net.tactware.wordprocessor.core.ByteOrder
import net.tactware.wordprocessor.core.WordProcessorFactory
import net.tactware.wordprocessor.core.WordSize
import net.tactware.wordprocessor.holder.extractToMessage
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class BitExtractorTest {
    
    private val bitExtractor = BasicBitExtractor()
    
    @Test
    fun `test extract bits from middle of word`() {
        // Value: 0b11010110 = 0xD6 = 214
        // Extract bits 2-5 (4 bits): 0b0101 = 5
        val value = 0xD6L
        val result = bitExtractor.extractBits(value, 2, 4)
        assertEquals(5L, result)
    }
    
    @Test
    fun `test extract bits from LSB`() {
        // Value: 0b11010110
        // Extract bits 0-3 (4 bits): 0b0110 = 6
        val value = 0xD6L
        val result = bitExtractor.extractBits(value, 0, 4)
        assertEquals(6L, result)
    }
    
    @Test
    fun `test extract bits from MSB`() {
        // Value: 0b11010110
        // Extract bits 4-7 (4 bits): 0b1101 = 13
        val value = 0xD6L
        val result = bitExtractor.extractBits(value, 4, 4)
        assertEquals(13L, result)
    }
    
    @Test
    fun `test extract single bit`() {
        val value = 0b10101010L
        assertEquals(0L, bitExtractor.extractBits(value, 0, 1))
        assertEquals(1L, bitExtractor.extractBits(value, 1, 1))
        assertEquals(0L, bitExtractor.extractBits(value, 2, 1))
        assertEquals(1L, bitExtractor.extractBits(value, 3, 1))
    }
    
    @Test
    fun `test extract all bits`() {
        val value = 0x123456789ABCDEFL
        val result = bitExtractor.extractBits(value, 0, 64)
        assertEquals(value, result)
    }
    
    @Test
    fun `test extract signed int positive`() {
        // Extract 8 bits: 0b01111111 = 127
        val value = 0x7FL
        val result = bitExtractor.extractInt(value, 0, 8)
        assertEquals(127, result)
    }
    
    @Test
    fun `test extract signed int negative`() {
        // Extract 8 bits: 0b11111111 = -1 (signed)
        val value = 0xFFL
        val result = bitExtractor.extractInt(value, 0, 8)
        assertEquals(-1, result)
    }
    
    @Test
    fun `test extract unsigned int`() {
        // Extract 8 bits: 0b11111111 = 255 (unsigned)
        val value = 0xFFL
        val result = bitExtractor.extractUInt(value, 0, 8)
        assertEquals(255L, result)
    }
    
    @Test
    fun `test extract signed short positive`() {
        val value = 0x7FFFL  // Max positive 16-bit value
        val result = bitExtractor.extractShort(value, 0, 16)
        assertEquals(32767.toShort(), result)
    }
    
    @Test
    fun `test extract signed short negative`() {
        val value = 0x8000L  // Min negative 16-bit value
        val result = bitExtractor.extractShort(value, 0, 16)
        assertEquals((-32768).toShort(), result)
    }
    
    @Test
    fun `test extract unsigned short`() {
        val value = 0xFFFFL
        val result = bitExtractor.extractUShort(value, 0, 16)
        assertEquals(65535, result)
    }
    
    @Test
    fun `test extract signed byte positive`() {
        val value = 0x7FL
        val result = bitExtractor.extractByte(value, 0, 8)
        assertEquals(127.toByte(), result)
    }
    
    @Test
    fun `test extract signed byte negative`() {
        val value = 0x80L
        val result = bitExtractor.extractByte(value, 0, 8)
        assertEquals((-128).toByte(), result)
    }
    
    @Test
    fun `test extract unsigned byte`() {
        val value = 0xFFL
        val result = bitExtractor.extractUByte(value, 0, 8)
        assertEquals(255, result)
    }
    
    @Test
    fun `test extract boolean true`() {
        val value = 0b10101010L
        assertTrue(bitExtractor.extractBoolean(value, 1))
        assertTrue(bitExtractor.extractBoolean(value, 3))
        assertTrue(bitExtractor.extractBoolean(value, 5))
        assertTrue(bitExtractor.extractBoolean(value, 7))
    }
    
    @Test
    fun `test extract boolean false`() {
        val value = 0b10101010L
        assertFalse(bitExtractor.extractBoolean(value, 0))
        assertFalse(bitExtractor.extractBoolean(value, 2))
        assertFalse(bitExtractor.extractBoolean(value, 4))
        assertFalse(bitExtractor.extractBoolean(value, 6))
    }
    
    @Test
    fun `test extract float`() {
        val floatValue = 3.14159f
        val bits = floatValue.toBits().toLong()
        val result = bitExtractor.extractFloat(bits, 0)
        assertEquals(floatValue, result, 0.00001f)
    }
    
    @Test
    fun `test extract double`() {
        val doubleValue = 3.141592653589793
        val bits = doubleValue.toBits()
        val result = bitExtractor.extractDouble(bits, 0)
        assertEquals(doubleValue, result, 0.0000000000001)
    }
    
    @Test
    fun `test extract bits across two words`() {
        // Word 0: 0xFFFF (16 bits all 1s)
        // Word 1: 0x0000 (16 bits all 0s)
        // Extract 20 bits starting from bit 8 of word 0
        // Should get: 8 bits from word 0 (all 1s) + 12 bits from word 1 (all 0s)
        // Result: 0b00000000000011111111 = 0xFF
        val words = listOf(0xFFFFL, 0x0000L)
        val result = bitExtractor.extractBitsAcrossWords(words, 0, 8, 20)
        assertEquals(0xFFL, result)
    }
    
    @Test
    fun `test extract bits across three words`() {
        // Extract bits spanning multiple words
        val words = listOf(0xFFFFL, 0xAAAAL, 0x5555L)
        val result = bitExtractor.extractBitsAcrossWords(words, 0, 0, 48)
        // Should combine all three 16-bit words
        // Word 0 bits go to result bits 0-15, word 1 to 16-31, word 2 to 32-47
        val expected = 0x5555AAAAFFFFL
        assertEquals(expected, result)
    }
    
    @Test
    fun `test message bit processor with single word`() {
        val processor = WordProcessorFactory.create(WordSize.WORD_16, ByteOrder.BIG_ENDIAN)
        val bytes = byteArrayOf(0xAB.toByte(), 0xCD.toByte())  // 0xABCD
        val message = processor.extractToMessage(bytes, 1)
        
        val bitProcessor = MessageBitProcessor(message)
        
        // Extract lower 8 bits: 0xCD = 205
        val result = bitProcessor.extractBits(0, 0, 8)
        assertEquals(0xCDL, result)
        
        // Extract upper 8 bits: 0xAB = 171
        val result2 = bitProcessor.extractBits(0, 8, 8)
        assertEquals(0xABL, result2)
    }
    
    @Test
    fun `test message bit processor with bit fields`() {
        val processor = WordProcessorFactory.create(WordSize.WORD_16, ByteOrder.BIG_ENDIAN)
        
        // Create a word with packed fields:
        // Bits 0-3: 0b1010 = 10
        // Bits 4-7: 0b0101 = 5
        // Bits 8-11: 0b1100 = 12
        // Bits 12-15: 0b0011 = 3
        // Combined: 0b0011110001011010 = 0x3C5A
        val bytes = byteArrayOf(0x3C, 0x5A)
        val message = processor.extractToMessage(bytes, 1)
        
        val bitFields = mapOf(
            "field1" to BitField("field1", 0, 0, 4, BitFieldType.UINT),
            "field2" to BitField("field2", 0, 4, 4, BitFieldType.UINT),
            "field3" to BitField("field3", 0, 8, 4, BitFieldType.UINT),
            "field4" to BitField("field4", 0, 12, 4, BitFieldType.UINT)
        )
        
        val bitProcessor = MessageBitProcessor(message)
        val results = bitProcessor.extractFields(bitFields)
        
        assertEquals(10L, results["field1"])
        assertEquals(5L, results["field2"])
        assertEquals(12L, results["field3"])
        assertEquals(3L, results["field4"])
    }
    
    @Test
    fun `test message extension functions`() {
        val processor = WordProcessorFactory.create(WordSize.WORD_16, ByteOrder.BIG_ENDIAN)
        val bytes = byteArrayOf(0xFF.toByte(), 0x00)  // 0xFF00
        val message = processor.extractToMessage(bytes, 1)
        
        // Test extension functions
        assertEquals(0x00L, message.extractBits(0, 0, 8))
        assertEquals(0xFFL, message.extractBits(0, 8, 8))
        assertTrue(message.extractBoolean(0, 8))
        assertFalse(message.extractBoolean(0, 0))
    }
    
    @Test
    fun `test ICD scenario - extract fields from words 4 to 8`() {
        val processor = WordProcessorFactory.create(WordSize.WORD_16, ByteOrder.BIG_ENDIAN)
        
        // Create a 10-word message
        val bytes = ByteArray(20) { it.toByte() }
        val message = processor.extractToMessage(bytes, 10)
        
        // Define bit fields in words 4-8 (indices 3-7)
        val bitFields = mapOf(
            "status_flag" to BitField("status_flag", 3, 0, 1, BitFieldType.BOOLEAN),
            "sensor_id" to BitField("sensor_id", 3, 1, 7, BitFieldType.UINT),
            "temperature" to BitField("temperature", 4, 0, 12, BitFieldType.INT),
            "pressure" to BitField("pressure", 5, 0, 16, BitFieldType.UINT),
            "altitude" to BitField("altitude", 6, 0, 16, BitFieldType.INT),
            "checksum" to BitField("checksum", 7, 0, 8, BitFieldType.UINT)
        )
        
        val results = message.extractFields(bitFields)
        
        // Verify we can extract all fields
        assertTrue(results.containsKey("status_flag"))
        assertTrue(results.containsKey("sensor_id"))
        assertTrue(results.containsKey("temperature"))
        assertTrue(results.containsKey("pressure"))
        assertTrue(results.containsKey("altitude"))
        assertTrue(results.containsKey("checksum"))
    }
}
