package com.kafka.producer.demo

import com.kafka.producer.demo.notification.Notification
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType

/** @author Vadzim_Kavalkou */
internal class NotificationIntegrationTest: IntegrationTest() {

    @Autowired
    lateinit var restTemplate: TestRestTemplate

    @Test
    fun `test creating a new notification`() {

        // given
        val headers = HttpHeaders()
        headers.accept = listOf(MediaType.APPLICATION_JSON)
        val request = HttpEntity(Notification("from", "to"), headers)

        // when
        val actual = restTemplate.exchange("/api/notifications", HttpMethod.POST, request, Void::class.java)

        // then
        assertEquals(HttpStatus.NO_CONTENT, actual.statusCode)
    }
}