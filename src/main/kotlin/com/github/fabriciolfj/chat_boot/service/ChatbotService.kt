package com.github.fabriciolfj.chat_boot.service

import com.github.fabriciolfj.chat_boot.model.dto.ChatResponse
import dev.langchain4j.data.message.AiMessage
import dev.langchain4j.data.message.UserMessage
import dev.langchain4j.data.segment.TextSegment
import dev.langchain4j.model.chat.ChatLanguageModel
import dev.langchain4j.model.embedding.EmbeddingModel
import dev.langchain4j.store.embedding.EmbeddingStore
import dev.langchain4j.store.embedding.EmbeddingSearchRequest
import mu.KotlinLogging
import org.springframework.stereotype.Service

@Service
class ChatbotService(
    private val chatModel: ChatLanguageModel,
    private val embeddingModel: EmbeddingModel,
    private val embeddingStore: EmbeddingStore<TextSegment>) {

    private val logger = KotlinLogging.logger {}

    private val maxResults = 3

    private val minScore = 0.7

    fun chat(userMessage: String): ChatResponse {
        try {
            logger.info { "received chat request: $userMessage" }

            val searchRequest = embeddingSearchRequest(userMessage)

            val searchResult = embeddingStore.search(searchRequest)
            val relevantDocuments = searchResult.matches()

            if (relevantDocuments.isEmpty()) {
                return getDefaultMessage(userMessage)
            }

            val context = relevantDocuments.joinToString("\n\n") {
                it.embedded().text()
            }

            val sources = relevantDocuments.mapNotNull { it.embedded().metadata().getString("title")?.toString() }.distinct()

            val formattedMessage = """
                Aqui estão informações relevantes que podem ajudar a responder:
                
                $context
                
                Com base nas informações acima, responda: $userMessage
            """.trimIndent()

            val messages = listOf(
                UserMessage(formattedMessage)
            )

            val aiMessage = chatModel.generate(messages) as AiMessage
            val response = aiMessage.text()

            logger.info { "Generated response for the user" }

            return ChatResponse(
                message = response,
                sources = sources
            )

        } catch (e: Exception) {
            logger.error(e) { "Error in chat: ${e.message}" }
            return ChatResponse(
                message = "Desculpe, ocorreu um erro ao processar sua mensagem. Por favor, tente novamente mais tarde.",
                sources = emptyList()
            )
        }
    }

    private fun getDefaultMessage(userMessage: String): ChatResponse {
        val messages = listOf(
            UserMessage(userMessage)
        )

        val aiMessage = chatModel.generate(messages) as AiMessage
        val response = aiMessage.text()

        return ChatResponse(
            message = response,
            sources = emptyList()
        )
    }

    private fun embeddingSearchRequest(userMessage: String): EmbeddingSearchRequest? {
        val queryEmbedding = embeddingModel.embed(userMessage).content()

        val searchRequest = EmbeddingSearchRequest.builder()
            .queryEmbedding(queryEmbedding)
            .maxResults(maxResults)
            .minScore(minScore)
            .build()
        return searchRequest
    }
}