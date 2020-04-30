package org.hexahexes.bitwisemadnessbot.api

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class HelloController {

    @GetMapping("hello")
    fun hello() = Hello("hello")

    data class Hello(val text: String)
}