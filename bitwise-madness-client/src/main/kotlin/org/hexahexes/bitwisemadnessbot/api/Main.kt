package org.hexahexes.bitwisemadnessbot.api

import org.javacord.api.DiscordApiBuilder


object Main {
    fun main() {
        val token  = Configurations.TOKEN
        DiscordApiBuilder()
                .setToken(token)
                .login()
                .thenAccept {
                    it.addMessageCreateListener(MessageCreateHandler)
                }
    }
}