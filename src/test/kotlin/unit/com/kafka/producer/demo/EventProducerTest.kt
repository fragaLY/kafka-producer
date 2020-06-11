package com.kafka.producer.demo

import com.fasterxml.jackson.databind.ObjectMapper
import com.kafka.producer.demo.notification.EventProducer
import com.kafka.producer.demo.notification.EventType
import com.kafka.producer.demo.notification.Notification
import com.kafka.producer.demo.notification.NotificationEvent
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.clients.producer.RecordMetadata
import org.apache.kafka.common.TopicPartition
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.kafka.support.SendResult
import org.springframework.util.concurrent.SettableListenableFuture
import java.util.UUID

/** @author Vadzim_Kavalkou */
@ExtendWith(MockitoExtension::class)
class EventProducerTest {

    @Mock
    lateinit var template: KafkaTemplate<UUID, String>

    @Mock
    lateinit var mapper: ObjectMapper

    @InjectMocks
    lateinit var producer: EventProducer

    @Test
    fun `test event producer on failure`() {
        // given
        val uuid = UUID.randomUUID()
        val notification = Notification(null, "from", "to")
        val json = mapper.writeValueAsString(notification)
        val event = NotificationEvent(uuid, notification, EventType.NOTIFICATION_CREATED)
        val future = SettableListenableFuture<SendResult<UUID, String>>()
        future.setException(RuntimeException("Exception calling Kafka"))
        Mockito.`when`(mapper.writeValueAsString(notification)).thenReturn(json)
        Mockito.`when`(template.sendDefault(uuid, json)).thenReturn(future)

        // when
        producer.produce(event)

        // then
        Mockito.verify(template).sendDefault(uuid, json)
    }

    @Test
    fun `test event producer on success`() {
        // given
        val uuid = UUID.randomUUID()
        val notification = Notification(null, "from", "to")
        val json = mapper.writeValueAsString(notification)
        val event = NotificationEvent(uuid, notification, EventType.NOTIFICATION_CREATED)

        val record = ProducerRecord("notification-event", event.id, json)
        val partition = TopicPartition("notification-event", 1)
        val meta = RecordMetadata(partition, 1, 1, 342, System.currentTimeMillis(), 1, 2)
        val result = SendResult(record, meta)
        val future = SettableListenableFuture<SendResult<UUID, String>>()
        future.set(result)

        Mockito.`when`(mapper.writeValueAsString(notification)).thenReturn(json)
        Mockito.`when`(template.sendDefault(uuid, json)).thenReturn(future)

        // when
        producer.produce(event)
    }
}