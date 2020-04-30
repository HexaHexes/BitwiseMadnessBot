package org.hexahexes.bitwisemadnessbot.api

import io.ktor.client.request.get
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.hexahexes.bitwisemadnessbot.api.message.MessageFormatStatus
import org.hexahexes.bitwisemadnessbot.api.message.MessageFormatter
import org.javacord.api.event.message.MessageCreateEvent
import org.javacord.api.listener.message.MessageCreateListener

object MessageCreateHandler : MessageCreateListener {

    val client = Configurations.CLIENT
    val botPrefix = Configurations.PREFIX
    val router = Configurations.ROUTER

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
            val url = commandRoute.urlWithReplacedArgs(messageFormatter.args)
            GlobalScope.launch {
                val response = client.get<Response>(url)
                channel.sendMessage(response.text)
            }
        }
    }
}