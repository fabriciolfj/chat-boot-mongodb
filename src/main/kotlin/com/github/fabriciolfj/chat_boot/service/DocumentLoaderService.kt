package com.github.fabriciolfj.chat_boot.service

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.github.fabriciolfj.chat_boot.model.domain.ArticleInfo
import dev.langchain4j.data.document.Document
import dev.langchain4j.data.document.DocumentSplitter
import dev.langchain4j.data.document.Metadata
import dev.langchain4j.data.document.splitter.DocumentSplitters
import dev.langchain4j.data.segment.TextSegment
import dev.langchain4j.model.embedding.EmbeddingModel
import dev.langchain4j.store.embedding.EmbeddingStore
import jakarta.annotation.PostConstruct
import mu.KotlinLogging
import org.springframework.beans.factory.annotation.Value
import org.springframework.core.io.Resource
import org.springframework.stereotype.Service
import java.nio.charset.StandardCharsets

@Service
class DocumentLoaderService(
    private val embeddingModel: EmbeddingModel,
    private val embeddingStore: EmbeddingStore<TextSegment>,
    private val objectMapper: ObjectMapper) {

    @Value("classpath:articles.json")
    private lateinit var articlesResource: Resource

    private val documentSplitter: DocumentSplitter = DocumentSplitters.recursive(300, 0)

    private val logger = KotlinLogging.logger {}

    @PostConstruct
    fun init() {
        loadArticles()
    }

    private fun loadArticles() {
        try {
            val articlesJson = articlesResource.inputStream.readAllBytes()
                .toString(StandardCharsets.UTF_8)

            val articles: List<ArticleInfo> = objectMapper.readValue(articlesJson)

            for (article in articles) {
                val document = Document(
                    article.content,
                    Metadata(
                        mapOf(
                            "title" to article.title,
                            "url" to article.url
                        )
                    )
                )

                val segments = documentSplitter.split(document)

                segments.forEach { segment ->
                    val embedding = embeddingModel.embed(segment.text()).content()
                    embeddingStore.add(embedding, segment)
                }

                logger.info { "Processed article: ${article.title}" }
            }

            logger.info { "Loaded ${articles.size} articles into the embedding store" }

        } catch (e: Exception) {
            logger.error(e) { "Error loading articles: ${e.message}" }
        }
    }
}