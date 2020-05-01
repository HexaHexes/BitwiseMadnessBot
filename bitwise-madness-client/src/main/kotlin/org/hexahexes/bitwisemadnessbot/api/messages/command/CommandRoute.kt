package org.hexahexes.bitwisemadnessbot.api.messages.command

import io.ktor.http.HttpMethod
import org.javacord.api.entity.server.Server
import java.lang.StringBuilder
import java.util.*
import kotlin.collections.HashMap

class CommandRoute(val method: HttpMethod, private val url: String, private val argIds: Array<String>) {

    fun getUrl(messageId: String, channelId: Long, originServer: Optional<Server>): String {

        val urlBuilder =
                StringBuilder("http://")
                .append(url)
                .append("?messageId=")
                .append(messageId)
                .append("&channelId=")
                .append(channelId)

        originServer.map {
            urlBuilder.append("&serverId=").append(it.id)
        }

        return urlBuilder.toString()
    }

    // might have errors when argIds dont have the same length as argValues
    fun getArgs(argValues: List<String>): HashMap<String,String> {
        val argsMap = HashMap<String,String>()
        argIds.zip(argValues).forEach {
            argsMap[it.first] = it.second
        }
        return argsMap
    }
}
