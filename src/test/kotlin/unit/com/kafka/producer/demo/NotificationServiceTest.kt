package com.kafka.producer.demo

import com.kafka.producer.demo.notification.EventProducer
import com.kafka.producer.demo.notification.EventType
import com.kafka.producer.demo.notification.Notification
import com.kafka.producer.demo.notification.NotificationEvent
import com.kafka.producer.demo.notification.NotificationService
import com.kafka.producer.demo.notification.UUIDProvider
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.jupiter.MockitoExtension
import java.util.UUID

/** @author Vadzim_Kavalkou */
@ExtendWith(MockitoExtension::class)
class NotificationServiceTest {

    @Mock
    lateinit var uuid: UUIDProvider

    @Mock
    lateinit var producer: EventProducer

    @InjectMocks
    lateinit var service: NotificationService

    @Test
    fun `test create notification event`() {
        // given
        val notification = Notification(null,"from", "to")
        val uuidValue = UUID.randomUUID()
        val event = NotificationEvent(uuidValue, notification, EventType.NOTIFICATION_CREATED)
        Mockito.`when`(uuid.random()).thenReturn(uuidValue)
        Mockito.doNothing().`when`(producer).produce(event)

        // when
        service.create(notification)

        // then
        Mockito.verify(producer).produce(event)
    }
}