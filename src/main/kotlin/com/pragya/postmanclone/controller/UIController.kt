package com.pragya.postmanclone.controller


import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono

@Controller
class UIController {

    @GetMapping("/")
    fun home(exchange: ServerWebExchange): Mono<String> {
        return exchange.session.flatMap {
            val user = it.attributes["user"] as? String
            if (user == null) Mono.just("redirect:/login")
            else Mono.just("home") // We'll add this page next
        }
    }
}