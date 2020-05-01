package org.hexahexes.bitwisemadnessbot.api

import io.ktor.client.request.request
import io.ktor.client.request.url
import io.ktor.util.error
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.hexahexes.bitwisemadnessbot.api.configuration.Configurations
import org.hexahexes.bitwisemadnessbot.api.messages.message.MessageFormatStatus
import org.hexahexes.bitwisemadnessbot.api.messages.message.MessageFormatter
import org.javacord.api.event.message.MessageCreateEvent
import org.javacord.api.listener.message.MessageCreateListener
import org.slf4j.LoggerFactory

object MessageCreateHandler : MessageCreateListener {
    private val logger    = LoggerFactory.getLogger(this.javaClass)
    private val client    = Configurations.CLIENT
    private val botPrefix = Configurations.PREFIX
    private val router    = Configurations.ROUTER

    const val ERROR_MESSAGE: String = "No Such Command!"

    // response format
    data class Response(val text: String)

    override fun onMessageCreate(event: MessageCreateEvent?) {
        logger.info("Received event")
        if(event == null) {
            logger.warn("Received event is 'null'")
            return
        }
        logger.info("Received message ${event.messageId} from ${event.message.author.displayName}: \"${event.message.content}\"")
        val messageFormatter = MessageFormatter(event, botPrefix)
        if (messageFormatter.formatStatus != MessageFormatStatus.OK) {
            logger.warn("Malformed Message: ${messageFormatter.formatStatus}")
            return
        }

        val channel = event.channel
        val commandRoute = router[messageFormatter.command]
        if (commandRoute == null) {
            channel.sendMessage(ERROR_MESSAGE)
        } else {
            val messageId = event.message.idAsString
            val requestUrl = commandRoute.getUrl(messageId)
            val args = commandRoute.getArgs(messageFormatter.args)
            val json = io.ktor.client.features.json.defaultSerializer()

            GlobalScope.launch {
                try {
                    val response: Response? = client.request<Response> {
                        logger.info("Sending Request for Message $messageId")

                        url(requestUrl)
                        logger.info("URL: $requestUrl")

                        //contentType(ContentType.Application.Json)

                        method = commandRoute.method
                        logger.info("METHOD: $method")

                        body = json.write(args)
                        logger.info("BODY: $body")
                    }

                    if(response != null) {
                        val text = response.text
                        channel.sendMessage(text)
                        logger.info("Response text from service to message $messageId -> $text")
                    } else {
                        logger.info("No response text from service to message $messageId")
                    }
                } catch (e: Exception) {
                    logger.error(e)
                }
            }
        }
    }
}