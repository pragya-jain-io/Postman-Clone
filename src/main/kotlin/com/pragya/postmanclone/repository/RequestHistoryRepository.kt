package com.pragya.postmanclone.repository

import com.pragya.postmanclone.model.RequestHistory
import org.springframework.data.mongodb.repository.ReactiveMongoRepository
import reactor.core.publisher.Flux

interface RequestHistoryRepository : ReactiveMongoRepository<RequestHistory, String> {
    fun findByUserOrderByTimestampDesc(user: String): Flux<RequestHistory>

}