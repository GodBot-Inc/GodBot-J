package ktCommands

import commands.Command.applicationId
import ktSnippets.standardError
import ktUtils.*
import net.dv8tion.jda.api.EmbedBuilder
import singeltons.AudioPlayerManagerWrapper
import singeltons.JDAManager
import singeltons.PlayerVault
import snippets.Colours
import snippets.ErrorMessages
import utils.EventExtender

fun clearQueue(event: EventExtender, payload: SlashCommandPayload) {
    val player: AudioPlayerExtender

    try {
        player = PlayerVault
            .getInstance()
            .getPlayer(
                JDAManager.getInstance().getJDA(applicationId),
                payload.guild.id
            )
    } catch (e: GuildNotFoundException) {
        event.replyEphemeral(
            standardError(
                ErrorMessages.NO_PLAYER_IN_GUILD
            )
        )
        return
    } catch (e: ChannelNotFoundException) {
        event.replyEphemeral(
            standardError(
                ErrorMessages.NO_PLAYER_IN_VC
            )
        )
        return
    }

    if (player.voiceChannel.id != payload.voiceChannel.id) {
        event.replyEphemeral(
            standardError(
                ErrorMessages.NO_PLAYER_IN_VC
            )
        )
        return
    }

    val items: Int = player.queue.size
    player.clearQueue()

    event.reply(
        EmbedBuilder()
            .setTitle(
                String.format(
                    "Cleared Queue: `%s` Items",
                    items
                )
            )
            .setColor(Colours.godbotYellow)
            .build()
    )
}

fun leave(event: EventExtender, payload: SlashCommandPayload) {
    val player: AudioPlayerExtender

    try {
        player = AudioPlayerManagerWrapper
            .getInstance()
            .getPlayer(
                JDAManager.getInstance().getJDA(applicationId),
                payload.guild.id,
                payload.voiceChannel
            )
    } catch (e: JDANotFoundException) {
        event.replyEphemeral(
            standardError(
                ErrorMessages.PLAYER_NOT_FOUND
            )
        )
        return
    } catch (e: GuildNotFoundException) {
        event.replyEphemeral(
            standardError(
                ErrorMessages.NO_PLAYER_IN_GUILD
            )
        )
        return
    }

    if (player.voiceChannel.id != payload.voiceChannel.id) {
        event.replyEphemeral(
            standardError(
                ErrorMessages.NO_PLAYER_IN_VC
            )
        )
        return
    }

    player.stop()
    player.closeConnection()

    event.reply(
        EmbedBuilder()
            .setTitle(
                String.format(
                    "Left Channel %s",
                    payload.voiceChannel.name
                )
            )
            .setColor(Colours.godbotYellow)
            .build()
    )
}
