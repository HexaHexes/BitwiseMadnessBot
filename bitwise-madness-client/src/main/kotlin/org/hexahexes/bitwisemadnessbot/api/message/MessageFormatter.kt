package org.hexahexes.bitwisemadnessbot.api.message

import org.javacord.api.event.message.MessageCreateEvent
import java.util.*

class MessageFormatter(messageCreateEvent: MessageCreateEvent, expectedPrefix: String) {
    val prefix: String
    val command: String
    val args: LinkedList<String> = LinkedList()
    val formatStatus: MessageFormatStatus

    init {
        val split = messageCreateEvent.messageContent.split(" ")
        val size = split.size

        // if has no prefix and no command and prefix isn't the bot's prefix, message is malformed
        if(size < 2) {
            formatStatus = MessageFormatStatus.INCORRECT_SIZE
            prefix = ""; command = ""
        } else if(split[0] != expectedPrefix) {
            formatStatus = MessageFormatStatus.PREFIX_UNMATCHED
            prefix = ""; command = ""
        } else {
            formatStatus = MessageFormatStatus.OK
            prefix = split[0]
            command = split[1]

            // has query arguments
            if(size > 2) {
                args.addAll(
                        split.drop(2)
                )
            }
        }
    }
}
