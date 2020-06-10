package com.kafka.producer.demo.notification

import com.fasterxml.jackson.databind.ObjectMapper
import org.slf4j.LoggerFactory
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.kafka.support.SendResult
import org.springframework.lang.Nullable
import org.springframework.stereotype.Component
import org.springframework.stereotype.Service
import org.springframework.util.concurrent.ListenableFutureCallback
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.UUID
import javax.validation.Valid
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull

enum class EventType { NOTIFICATION_CREATED }

data class Notification(
    @field:NotBlank(message = "The sender should not be blank") val from: String,
    @field:NotBlank(message = "The receiver should not be blank") val to: String
)

data class NotificationEvent(val id: UUID, val notification: Notification, val type: EventType)

@Service
class NotificationService(private val uuid: UUIDProvider, private val producer: EventProducer) {

    fun create(notification: Notification) =
        producer.produce(NotificationEvent(uuid.random(), notification, EventType.NOTIFICATION_CREATED))
}

@RequestMapping("/api/notifications")
interface Api {
    @PostMapping(consumes = [MediaType.APPLICATION_JSON_VALUE])
    fun create(@NotNull(message = "The notification cannot be null") @Valid @RequestBody notification: Notification): ResponseEntity<Unit>
}

@RestController
class Controller(private val service: NotificationService) : Api {
    override fun create(notification: Notification) =
        service.create(notification).run { ResponseEntity.noContent().build<Unit>() }
}

@Component
class UUIDProvider {
    fun random(): UUID = UUID.randomUUID()
}

@Component
class EventProducer(private val template: KafkaTemplate<UUID, String>, private val mapper: ObjectMapper) {

    private val logger = LoggerFactory.getLogger(javaClass)

    fun produce(event: NotificationEvent) {
        val result = template.sendDefault(event.id, mapper.writeValueAsString(event.notification))
        result.addCallback(object : ListenableFutureCallback<SendResult<UUID, String>> {
            override fun onFailure(exception: Throwable) =
                logger.error("[NOTIFICATION EVENT] Error producing event [$exception]")

            override fun onSuccess(@Nullable result: SendResult<UUID, String>?) =
                logger.info("[NOTIFICATION EVENT] The event successfully produced a message in topic [${result?.recordMetadata?.topic()}] in partition [${result?.recordMetadata?.partition()}] with offset [${result?.recordMetadata?.offset()}]")
        })
    }
}