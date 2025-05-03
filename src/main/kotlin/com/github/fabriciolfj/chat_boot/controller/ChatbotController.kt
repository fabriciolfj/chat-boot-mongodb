package com.github.fabriciolfj.chat_boot.controller

import com.github.fabriciolfj.chat_boot.model.dto.ChatRequest
import com.github.fabriciolfj.chat_boot.model.dto.ChatResponse
import com.github.fabriciolfj.chat_boot.service.ChatbotService
import mu.KotlinLogging
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api")
class ChatbotController(private val chatbotService: ChatbotService) {

    private val logger = KotlinLogging.logger {  }

    @PostMapping
    fun send(@RequestBody chatRequest: ChatRequest) : ChatResponse {
        logger.info { "receive message ${chatRequest.message}" }
        return chatbotService.chat(chatRequest.message)
    }
}