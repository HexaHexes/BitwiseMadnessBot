package org.hexahexes.bitwisemadnessbot.api

import org.hexahexes.bitwisemadnessbot.api.configuration.Configurations
import org.javacord.api.DiscordApiBuilder

fun main() {
    DiscordApiBuilder()
            .setToken(Configurations.TOKEN)
            .login()
            .thenAccept {
                it.addMessageCreateListener(MessageCreateHandler)
            }
    println("Bot Running...")
}