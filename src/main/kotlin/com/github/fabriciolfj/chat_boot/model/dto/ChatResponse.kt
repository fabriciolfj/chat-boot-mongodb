package com.github.fabriciolfj.chat_boot.model.dto

data class ChatResponse(
    val message: String,
    val sources: List<String> = emptyList()
)