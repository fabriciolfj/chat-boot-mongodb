package com.github.fabriciolfj.chat_boot

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class ChatBootApplication

fun main(args: Array<String>) {
	runApplication<ChatBootApplication>(*args)
}
