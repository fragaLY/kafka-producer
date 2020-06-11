package com.kafka.producer.demo

import com.fasterxml.jackson.databind.ObjectMapper
import com.kafka.producer.demo.notification.Controller
import com.kafka.producer.demo.notification.Notification
import com.kafka.producer.demo.notification.NotificationService
import org.junit.jupiter.api.Test
import org.mockito.Mockito.doNothing
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

/** @author Vadzim_Kavalkou */
@WebMvcTest(Controller::class)
@AutoConfigureMockMvc
class ControllerUnitTest {

    @Autowired
    lateinit var mvc: MockMvc

    @Autowired
    lateinit var mapper: ObjectMapper

    @MockBean
    lateinit var service: NotificationService

    @Test
    fun `create notification event test when payload is valid`() {
        // given
        val notification = Notification("from", "to")
        val payload = mapper.writeValueAsString(notification)

        doNothing().`when`(service).create(notification)

        // when
        mvc.perform(post("/api/notifications").content(payload).contentType(MediaType.APPLICATION_JSON))
            //then
            .andExpect(status().isNoContent)
    }

    @Test
    fun `create notification event test when payload has no first argument`() {
        // given
        val notification = Notification("", "to")
        val payload = mapper.writeValueAsString(notification)

        doNothing().`when`(service).create(notification)

        // when
        mvc.perform(post("/api/notifications").content(payload).contentType(MediaType.APPLICATION_JSON))
            //then
            .andExpect(status().isBadRequest)
    }

    @Test
    fun `create notification event test when payload has no second argument`() {
        // given
        val notification = Notification("", "to")
        val payload = mapper.writeValueAsString(notification)

        doNothing().`when`(service).create(notification)

        // when
        mvc.perform(post("/api/notifications").content(payload).contentType(MediaType.APPLICATION_JSON))
            //then
            .andExpect(status().isBadRequest)
    }

    @Test
    fun `create notification event test when payload has no two arguments`() {
        // given
        val notification = Notification("", "")
        val payload = mapper.writeValueAsString(notification)

        doNothing().`when`(service).create(notification)

        // when
        mvc.perform(post("/api/notifications").content(payload).contentType(MediaType.APPLICATION_JSON))
            //then
            .andExpect(status().isBadRequest)
    }
}