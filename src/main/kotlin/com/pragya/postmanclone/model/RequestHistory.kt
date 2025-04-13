package com.pragya.postmanclone.model

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.time.Instant

@Document
data class RequestHistory(
    @Id val id: String? = null,
    val user: String,
    val method: String,
    val url: String,
    val headers: String,
    val body: String,
    val response: String,
    val timestamp: Instant = Instant.now()
)
