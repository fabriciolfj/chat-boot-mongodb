package com.github.fabriciolfj.chat_boot.config

import com.mongodb.client.MongoClient
import com.mongodb.client.MongoClients
import dev.langchain4j.store.embedding.mongodb.MongoDbEmbeddingStore
import dev.langchain4j.data.segment.TextSegment
import dev.langchain4j.model.anthropic.AnthropicChatModel
import dev.langchain4j.model.chat.ChatLanguageModel
import dev.langchain4j.model.embedding.EmbeddingModel
import dev.langchain4j.model.embedding.onnx.allminilml6v2q.AllMiniLmL6V2QuantizedEmbeddingModel
import dev.langchain4j.store.embedding.EmbeddingStore
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.DependsOn

@Configuration
class ModelConfig {

    @Value("\${anthropic.api.key}")
    private lateinit var anthropicApiKey: String

    @Value("\${anthropic.model}")
    private lateinit var anthropicModel: String

    @Value("\${mongodb.connection.string}")
    private lateinit var mongodbConnectionString: String

    @Value("\${mongodb.database}")
    private lateinit var mongodbDatabase: String

    @Value("\${mongodb.collection}")
    private lateinit var mongodbCollection: String

    @Bean
    fun chatLanguageModel(): ChatLanguageModel {
        return AnthropicChatModel.builder()
            .apiKey(anthropicApiKey)
            .modelName(anthropicModel)
            .maxTokens(1000)
            .temperature(0.7)
            .build()
    }

    @Bean
    fun embeddingModel(): EmbeddingModel {
        return AllMiniLmL6V2QuantizedEmbeddingModel()
    }

    @Bean
    fun mongoClient(): MongoClient {
        return MongoClients.create(mongodbConnectionString)
    }

    @Bean
    @DependsOn("mongoClient")
    fun embeddingStore(mongoClient: MongoClient): EmbeddingStore<TextSegment> {
        return MongoDbEmbeddingStore.builder()
            .fromClient(mongoClient)
            .indexName("test")
            .databaseName(mongodbDatabase)
            .collectionName(mongodbCollection)
            .build()
    }
}
