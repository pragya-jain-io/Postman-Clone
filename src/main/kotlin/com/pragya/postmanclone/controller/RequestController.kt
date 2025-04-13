package com.pragya.postmanclone.controller

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
    fun sendRequest(@ModelAttribute form: RequestForm, model: Model): Mono<String> {
        println("Inside sendRequest")
        val url = form.url

        if (url.isNullOrBlank()) {
            println("URL is null or blank!")
            model.addAttribute("response", "No URL provided")
            return Mono.just("home")
        }

        println("URL received: $url")

        return webClient
            .get()
            .uri(url)
            .retrieve()
            .bodyToMono(String::class.java)
            .map { response ->
                model.addAttribute("response", response)
                "home"
            }
            .onErrorResume {
                model.addAttribute("response", "Error: ${it.message}")
                Mono.just("home")
            }
    }





}