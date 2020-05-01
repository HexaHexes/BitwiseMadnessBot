package org.hexahexes.bitwisemadnessbot.api.messages.command

import io.ktor.http.HttpMethod
import org.hexahexes.bitwisemadnessbot.api.configuration.Configurations
import java.lang.StringBuilder

class CommandRoute(val method: HttpMethod, private val url: String, private val argIds: Array<String>) {

    fun getUrl(messageId: String): String {
        return StringBuilder("http://")
                .append(Configurations.SERVICES_ADDRESS)
                .append("/")
                .append(url)
                .append("?messageId=")
                .append(messageId)
                .toString()
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
