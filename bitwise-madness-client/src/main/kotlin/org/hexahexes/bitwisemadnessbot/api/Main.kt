package org.hexahexes.bitwisemadnessbot.api

import org.javacord.api.DiscordApiBuilder


object Main {
    fun main() {
        DiscordApiBuilder()
                .setToken(Configurations.TOKEN)
                .login()
                .thenAccept {
                    it.addMessageCreateListener(MessageCreateHandler)
                }
    }
}