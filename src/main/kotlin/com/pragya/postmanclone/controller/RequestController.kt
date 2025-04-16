package com.pragya.postmanclone.controller

import com.pragya.postmanclone.model.RequestHistory
import com.pragya.postmanclone.repository.RequestHistoryRepository
import org.springframework.http.HttpMethod
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ServerWebExchange
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.server.WebSession
import reactor.core.publisher.Mono
import java.time.Duration
import java.time.Instant


@Controller
class RequestController(private val webClientBuilder: WebClient.Builder,
                        private val requestRepo: RequestHistoryRepository){
    val webClient = webClientBuilder.build()

    @GetMapping("/")
    fun home(exchange: ServerWebExchange, model: Model): Mono<String> {
        return exchange.session.flatMap {
            val user = it.attributes["user"] as? String
            if (user == null) {
                Mono.just("redirect:/login")
            } else {
                model.addAttribute("user", user)
                Mono.just("home")
            }
        }
    }

    data class ResponseData(
        val body: String,
        val headers: String,
        val statusCode: Int,
        val timeTaken: Long
    )


    @PostMapping("/send-request")
    fun sendRequest(
        @ModelAttribute form: RequestForm,
        model: Model,
        exchange: ServerWebExchange
    ): Mono<String> {
        val headersMap = form.headers.lines()
            .mapNotNull {
                val parts = it.split(":", limit = 2)
                if (parts.size == 2) parts[0].trim() to parts[1].trim() else null
            }.toMap()
        println("====== HEADERS MAP ======")
        headersMap.forEach { (k, v) -> println("$k: $v") }
        println("====== END HEADERS MAP ======")

        var requestSpec = webClient.method(HttpMethod.valueOf(form.method.uppercase()))
            .uri(form.url)
            .headers { httpHeaders ->
                headersMap.forEach { (k, v) -> httpHeaders.set(k, v) }
            }
        println("====== REQUEST SPEC DEBUG ======")
        println("Method: ${form.method.uppercase()}")
        println("URL: ${form.url}")
        println("Body: ${form.body}")
        println("Headers added above")
        println("==============================")

        val startTime = System.currentTimeMillis()

        val responseMono = requestSpec.exchangeToMono { response ->
            println(">>> Exchange reached <<<")
            val timeTaken = System.currentTimeMillis() - startTime
            val statusCode = response.statusCode().value()
            val headers = response.headers().asHttpHeaders()
                .entries.joinToString("\n") { "${it.key}: ${it.value.joinToString()}" }

            println("Status Code: $statusCode")
            println("Time Taken: $timeTaken ms")
            println("Headers: $headers")

            response.bodyToMono(String::class.java)
                .defaultIfEmpty("")
                .map { body ->
                println("Response Body: $body")
                ResponseData(body, headers, statusCode, timeTaken)
            }
        }.doOnError {
            println("Error: ${it.message}")
        }.onErrorResume {    println(">>> ERROR CAUGHT: ${it.message}")
            Mono.just(ResponseData("Error: ${it.message}", "", 500, 0))}

        return responseMono.flatMap { resData ->
            exchange.session.flatMap { session ->
                val user = session.attributes["user"] as? String ?: "Unknown"

                val history = RequestHistory(
                    user = user,
                    method = form.method,
                    url = form.url,
                    headers = form.headers,
                    body = form.body,
                    response = resData.body.take(500),  // trimmed
                    timestamp = Instant.now()
                )

                requestRepo.save(history).then(
                    Mono.fromCallable {
                        model.addAttribute("response", resData.body)
                        model.addAttribute("responseHeaders", resData.headers)
                        model.addAttribute("statusCode", resData.statusCode)
                        model.addAttribute("responseTime", resData.timeTaken)
                        model.addAttribute("user", user)
                        model.addAttribute("form", form)
                        "home"
                    }
                )
            }
        }.onErrorResume {
            println(">>> FINAL FALLBACK ERROR: ${it.message}")
            model.addAttribute("error", it.message)
            Mono.just("home") // redirect to home even on failure
        }
    }





    @GetMapping("/history")
    fun showHistory(exchange: ServerWebExchange, model: Model): Mono<String> {
        return exchange.session.flatMap { session ->
            val user = session.attributes["user"] as? String
            if (user == null) {
                Mono.just("redirect:/login")
            } else {
                requestRepo.findByUserOrderByTimestampDesc(user).collectList().map { historyList ->
                    model.addAttribute("history", historyList)
                    model.addAttribute("user", user)
                    "history"
                }
            }
        }
    }





}