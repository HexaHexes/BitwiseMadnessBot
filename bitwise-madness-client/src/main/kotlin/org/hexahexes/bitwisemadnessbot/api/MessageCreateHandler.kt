package org.hexahexes.bitwisemadnessbot.api

import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.put
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.hexahexes.bitwisemadnessbot.api.configuration.Configurations
import org.hexahexes.bitwisemadnessbot.api.messages.message.MessageFormatStatus
import org.hexahexes.bitwisemadnessbot.api.messages.message.MessageFormatter
import org.hexahexes.bitwisemadnessbot.api.util.HttpMethod
import org.javacord.api.event.message.MessageCreateEvent
import org.javacord.api.listener.message.MessageCreateListener

object MessageCreateHandler : MessageCreateListener {

    private val client    = Configurations.CLIENT
    private val botPrefix = Configurations.PREFIX
    private val router    = Configurations.ROUTER

    const val ERROR_MESSAGE: String = "No Such Command!"

    // response format
    data class Response(val text: String)

    override fun onMessageCreate(event: MessageCreateEvent?) {

        if(event == null) {
            return
        }
        val messageFormatter = MessageFormatter(event, botPrefix)
        if (messageFormatter.formatStatus != MessageFormatStatus.OK) {
            return
        }
        val channel = event.channel
        val commandRoute = router[messageFormatter.command]
        if (commandRoute == null) {
            channel.sendMessage(ERROR_MESSAGE)
        } else {
            val messageId = event.message.idAsString
            val url = commandRoute.urlWithReplacedArgs(messageId, messageFormatter.args)
            GlobalScope.launch {
                val response: Response? = when(commandRoute.method) {
                    HttpMethod.GET    -> client.get<Response>(url)
                    HttpMethod.POST   -> client.post<Response>(url)
                    HttpMethod.PUT    -> client.put<Response>(url)
                    HttpMethod.DELETE -> client.delete<Response>(url)
                }
                if(response != null) {
                    channel.sendMessage(response.text)
                }
            }
        }
    }
}