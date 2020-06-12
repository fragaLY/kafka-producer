package com.kafka.producer.demo

import com.kafka.producer.demo.notification.UUIDProvider
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test
import java.util.UUID

/** @author Vadzim_Kavalkou */
class UUIDProviderTest {

    private val uuid = UUIDProvider()

    @Test
    fun `test uuid provider`() {
        val actual = uuid.random()
        assertNotNull(actual)
        assertEquals(UUID::class.java, actual::class.java)
    }
}