package org.hexahexes.bitwisemadnessbot.api

import java.lang.StringBuilder

class CommandRoute(private val url: String, private val argIds: Array<String>) {

    // might have errors when argIds dont have the same length as argValues
    fun urlWithReplacedArgs(argValues: List<String>): String {
        val urlBuilder = StringBuilder("http://").append(url)
        if(argValues.isNotEmpty()) {

            urlBuilder.append("?")

            argIds.zip(argValues).forEachIndexed { i, pair ->
                val (id, value) = pair
                if (i > 0) {
                    urlBuilder.append("&")
                }
                urlBuilder
                        .append(id)
                        .append("=")
                        .append(value)
            }
        }
        return urlBuilder.toString()
    }
}
