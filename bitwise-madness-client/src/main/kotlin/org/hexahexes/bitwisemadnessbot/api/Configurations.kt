package org.hexahexes.bitwisemadnessbot.api

import io.ktor.client.HttpClient

object Configurations {

    // TODO: loading configurations from config.json
    val ROUTER: HashMap<String,CommandRoute>
    val PREFIX: String
    val TOKEN: String
    val CLIENT = HttpClient()

    init {
        ROUTER = hashMapOf()
        PREFIX = "please load me"
        TOKEN = "please load me"
    }

}
