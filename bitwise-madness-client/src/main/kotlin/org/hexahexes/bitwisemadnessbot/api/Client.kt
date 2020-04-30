package org.hexahexes.bitwisemadnessbot.api

import io.ktor.client.HttpClient
import io.ktor.client.request.get
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import org.javacord.api.DiscordApiBuilder
import org.javacord.api.entity.DiscordClient
import org.javacord.api.event.message.MessageCreateEvent

class Client {



}

suspend fun main() {

    val token = ""
    val prefix = ":bot"
    val router = hashMapOf<String, String>()
    val client = HttpClient()

    DiscordApiBuilder()
            .setToken(token)
            .login()
            .thenAccept {
                it.addMessageCreateListener {
                    val message = Message(it)
                    if(message.prefix != prefix) {
                        return@addMessageCreateListener
                    }

                    val value = router[message.command]
                    if(value == null) {
                        message.respond("No such command!")
                    } else {
                        GlobalScope.launch {
                            val response = client.get<Response>("http://localhost:8080/hello")
                            message.respond(response.text)
                        }
                    }

                }
            }
}

class Response(
        val text: String
)

class Message(
        val messageCreateEvent: MessageCreateEvent
) {

    val prefix: String
    val command: String
    val args: List<String>

    init {
        val split = messageCreateEvent.messageContent.split(" ")

        prefix = split[0]
        command = split[1]
        args = split.drop(2)
    }

    fun respond(response: String) {
        messageCreateEvent.channel.sendMessage(response)
    }
}