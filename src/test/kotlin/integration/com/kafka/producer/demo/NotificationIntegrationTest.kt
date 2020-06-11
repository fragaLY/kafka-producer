package com.kafka.producer.demo

import com.kafka.producer.demo.notification.Notification
import org.apache.kafka.clients.consumer.Consumer
import org.apache.kafka.common.serialization.StringDeserializer
import org.apache.kafka.common.serialization.UUIDDeserializer
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Timeout
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.kafka.core.DefaultKafkaConsumerFactory
import org.springframework.kafka.test.EmbeddedKafkaBroker
import org.springframework.kafka.test.utils.KafkaTestUtils
import java.util.UUID

/** @author Vadzim_Kavalkou */
internal class NotificationIntegrationTest : IntegrationTest() {

    @Autowired
    lateinit var restTemplate: TestRestTemplate

    @Autowired
    lateinit var broker: EmbeddedKafkaBroker

    lateinit var consumer: Consumer<UUID, String>

    @BeforeEach
    internal fun setUp() {
        val properties = HashMap(KafkaTestUtils.consumerProps("group1", "true", broker))
        consumer = DefaultKafkaConsumerFactory(properties, UUIDDeserializer(), StringDeserializer()).createConsumer()
        broker.consumeFromAllEmbeddedTopics(consumer)
    }

    @AfterEach
    internal fun tearDown() {
        consumer.close()
    }
                    
    @Test
    @Timeout(3)
    fun `test creating a new notification`() {
        // given
        val headers = HttpHeaders()
        headers.accept = listOf(MediaType.APPLICATION_JSON)
        val request = HttpEntity(Notification("from", "to"), headers)

        // when
        val actual = restTemplate.exchange("/api/notifications", HttpMethod.POST, request, Void::class.java)

        // then
        assertEquals(HttpStatus.NO_CONTENT, actual.statusCode)
        assertEquals(
            """{"from":"from","to":"to"}""",
            KafkaTestUtils.getSingleRecord(consumer, "notification-event").value()
        )
    }
}