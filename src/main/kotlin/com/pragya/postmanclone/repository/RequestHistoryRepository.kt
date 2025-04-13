package com.pragya.postmanclone.repository

import com.pragya.postmanclone.model.RequestHistory
import org.springframework.data.mongodb.repository.ReactiveMongoRepository
import reactor.core.publisher.Flux

interface RequestHistoryRepository : ReactiveMongoRepository<RequestHistory, String> {
    fun findAllByUserOrderByTimestampDesc(user: String): Flux<RequestHistory>
    fun findByUser(user: String): Flux<RequestHistory>
}