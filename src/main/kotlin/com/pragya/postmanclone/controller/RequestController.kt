package com.pragya.postmanclone.controller

import org.springframework.http.HttpMethod
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ServerWebExchange
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.server.WebSession
import reactor.core.publisher.Mono

@Controller
class RequestController(private val webClientBuilder: WebClient.Builder) {
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

        var requestSpec = webClient.method(HttpMethod.valueOf(form.method.uppercase()))
            .uri(form.url)
            .headers { httpHeaders ->
                headersMap.forEach { (k, v) -> httpHeaders.set(k, v) }
            }

        val responseMono = if (form.method.uppercase() == "GET") {
            requestSpec.retrieve().bodyToMono(String::class.java)
        } else {
            requestSpec.bodyValue(form.body).retrieve().bodyToMono(String::class.java)
        }

        return responseMono
            .onErrorResume { Mono.just("Error: ${it.message}") }
            .flatMap {
                model.addAttribute("response", it)
                exchange.session.flatMap { session ->
                    val user = session.attributes["user"] as? String ?: "Unknown"
                    model.addAttribute("user", user)
                    Mono.just("home")
                }
            }
    }






}