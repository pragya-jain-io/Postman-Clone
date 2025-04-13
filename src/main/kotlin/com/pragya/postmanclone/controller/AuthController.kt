package com.pragya.postmanclone.controller

import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono

@Controller
class AuthController {

    @GetMapping("/login")
    fun showLogin(): String = "login"

    @PostMapping("/login")
    fun login(exchange: ServerWebExchange): Mono<String> {
        return exchange.formData.flatMap { data ->
            val username = data.getFirst("username")
            if (username.isNullOrBlank()) {
                Mono.just("redirect:/login")
            } else {
                exchange.session.flatMap {
                    it.attributes["user"] = username
                    Mono.just("redirect:/")
                }
            }
        }
    }


    @GetMapping("/logout")
    fun logout(exchange: ServerWebExchange): Mono<String> {
        return exchange.session.flatMap {
            it.invalidate().thenReturn("redirect:/login")
        }
    }
}