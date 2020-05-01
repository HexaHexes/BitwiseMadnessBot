package org.hexahexes.bitwisemadnessbot.api

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import io.ktor.client.request.request
import io.ktor.client.request.url
import io.ktor.client.statement.HttpResponse
import io.ktor.http.contentLength
import io.ktor.util.error
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.hexahexes.bitwisemadnessbot.api.configuration.Configurations
import org.hexahexes.bitwisemadnessbot.api.messages.message.MessageFormatStatus
import org.hexahexes.bitwisemadnessbot.api.messages.message.MessageFormatter
import org.javacord.api.event.message.MessageCreateEvent
import org.javacord.api.listener.message.MessageCreateListener
import org.slf4j.LoggerFactory
import java.nio.ByteBuffer

object MessageCreateHandler : MessageCreateListener {
    private val logger    = LoggerFactory.getLogger(this.javaClass)
    private val client    = Configurations.CLIENT
    private val botPrefix = Configurations.PREFIX
    private val router    = Configurations.ROUTER
    private val commandNotFoundText = "Command not found!"

    private const val TEXT_KEY: String  = "text"
    private const val ERROR_KEY: String = "error"

    private val RESPONSE_MAPPER = ObjectMapper()

    private suspend fun mapResponseContent(response: HttpResponse): JsonNode {
        val responseContent = response.content
        val buffer = ByteBuffer.allocate(response.contentLength()?.toInt()!!)
        responseContent.readFully(buffer)
        return withContext(Dispatchers.IO) {
            RESPONSE_MAPPER.readTree(buffer.toString())
        }
    }

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
            channel.sendMessage(commandNotFoundText)
        } else {
            val messageId = event.message.idAsString
            val channelId = event.channel.id
            val server    = event.server
            val requestUrl = commandRoute.getUrl(messageId, channelId, server)
            val args = commandRoute.getArgs(messageFormatter.args)
            val jsonSerializer = io.ktor.client.features.json.defaultSerializer()

            GlobalScope.launch {
                try {
                    val responseMap: HashMap<String,String> = client.request {
                        logger.info("Sending Request for Message $messageId")

                        url(requestUrl)
                        logger.info("URL: $requestUrl")

                        method = commandRoute.method
                        logger.info("METHOD: $method")

                        body = jsonSerializer.write(args)
                        logger.info("BODY: $body")
                    }

                    if(responseMap.isEmpty()) {
                        logger.info("No response text from service to message $messageId")
                    } else {
                        val messageResponse = when {
                            responseMap.containsKey(ERROR_KEY) -> {
                                responseMap[ERROR_KEY]
                            }
                            responseMap.containsKey(TEXT_KEY) -> {
                                responseMap[TEXT_KEY]
                            }
                            else -> {
                                val msgBuilder = StringBuilder()
                                responseMap.entries.map {
                                    msgBuilder.append(it.key).append(" -> ").append(it.value).append("\n")
                                }
                                msgBuilder.toString()
                            }
                        }
                        channel.sendMessage(messageResponse)
                        logger.info("Response text from service to message $messageId -> $messageResponse")
                    }
                } catch (e: Exception) {
                    logger.error(e)
                }
            }
        }
    }
}