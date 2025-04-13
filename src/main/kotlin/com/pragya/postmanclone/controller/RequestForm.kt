package com.pragya.postmanclone.controller

data class RequestForm(
    var url: String = "",
    var method: String = "GET",
    var headers: String = "",
    var body: String = ""
)