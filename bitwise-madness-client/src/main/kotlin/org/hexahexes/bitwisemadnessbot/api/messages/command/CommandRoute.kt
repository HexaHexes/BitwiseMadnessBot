package org.hexahexes.bitwisemadnessbot.api.messages.command

import org.hexahexes.bitwisemadnessbot.api.util.HttpMethod
import java.lang.StringBuilder

class CommandRoute(val method: HttpMethod, private val url: String, private val argIds: Array<String>) {

    // might have errors when argIds dont have the same length as argValues
    fun urlWithReplacedArgs(messageId: String, argValues: List<String>): String {
        val urlBuilder =
                StringBuilder("http://")
                        .append(url)
                        .append("?messageId=")
                        .append(messageId)

        argIds.zip(argValues).forEach {
            val (id, value) = it
            urlBuilder
                    .append("&")
                    .append(id)
                    .append("=")
                    .append(value)
        }

        return urlBuilder.toString()
    }
}
