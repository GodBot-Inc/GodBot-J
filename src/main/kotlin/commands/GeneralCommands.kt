package commands

import io.github.cdimascio.dotenv.Dotenv
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.entities.Guild
import net.dv8tion.jda.api.entities.Member
import net.dv8tion.jda.api.entities.VoiceChannel
import singeltons.AudioPlayerManagerWrapper
import singeltons.JDAManager
import singeltons.PlayerVault
import snippets.Colours
import snippets.ErrorMessages
import snippets.standardError
import utils.*

fun clearQueue(event: EventExtender) {
    val applicationId: String? = Dotenv.load()["APPLICATIONID"]
    val guild: Guild? = event.event.guild
    val member: Member? = event.event.member
    val voiceChannel: VoiceChannel

    val player: AudioPlayerExtender

    try {
        voiceChannel = Checks.slashCommandCheck(
            event,
            applicationId,
            member,
            guild
        )
    } catch (e: CheckFailedException) {
        return
    }

    try {
        player = PlayerVault
            .getInstance()
            .getPlayer(
                JDAManager.getInstance().getJDA(applicationId),
                guild!!.id
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

    if (player.voiceChannel.id != voiceChannel.id) {
        event.replyEphemeral(
            standardError(
                ErrorMessages.NO_PLAYER_IN_VC
            )
        )
        return
    }

    val items: Int = player.queue.size

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

fun leave(event: EventExtender) {
    val applicationId: String? = Dotenv.load()["APPLICATIONID"]
    val guild: Guild? = event.event.guild
    val member: Member? = event.event.member
    val voiceChannel: VoiceChannel

    try {
        voiceChannel = Checks.slashCommandCheck(
            event,
            applicationId,
            member,
            guild
        )
    } catch (e: CheckFailedException) {
        return
    }

    val player: AudioPlayerExtender

    try {
        player = AudioPlayerManagerWrapper
            .getInstance()
            .getPlayer(
                JDAManager.getInstance().getJDA(applicationId),
                guild!!.id,
                voiceChannel
            )
    } catch (e: JDANotFound) {
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

    if (player.voiceChannel.id != voiceChannel.id) {
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
                    "Left Channel `%s`",
                    voiceChannel.name
                )
            )
            .setColor(Colours.godbotYellow)
            .build()
    )
}
