package ktCommands

import commands.Command.applicationId
import ktSnippets.standardError
import ktSnippets.trackLines
import ktUtils.*
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent
import net.dv8tion.jda.api.interactions.commands.OptionMapping
import singeltons.AudioPlayerManagerWrapper
import singeltons.JDAManager
import singeltons.PlayerVault
import snippets.Colours
import snippets.EmojiIds
import snippets.ErrorMessages
import utils.DurationCalc
import utils.EventExtender
import java.util.concurrent.TimeUnit

fun remove(event: EventExtender, payload: SlashCommandPayload) {
    fun removeCheckParameters(event: SlashCommandEvent): Long {
        val position: OptionMapping = event.getOption("position") ?: throw ArgumentNotFoundException()
        return position.asLong
    }

    val position: Int

    try {
        position = removeCheckParameters(event.event).toInt()
    } catch (e: CheckFailedException) {
        event.replyEphemeral(
            standardError(
                ErrorMessages.NOT_RECEIVED_PARAMETER
            )
        )
        return
    }

    val audioPlayer: AudioPlayerExtender

    try {
        audioPlayer = PlayerVault
            .getInstance()
            .getPlayer(
                JDAManager.getInstance().getJDA(applicationId),
                payload.guild.id
            )
    } catch (e: PlayerNotFoundException) {
        event.replyEphemeral(
            standardError(
                ErrorMessages.NO_PLAYER_IN_VC
            )
        )
        return
    }

    val audioTrack: AudioTrackExtender

    try {
        audioTrack = audioPlayer.removeTrackAt(position - 1)
    } catch (e: IndexOutOfBoundsException) {
        event.replyEphemeral(
            standardError(
                String.format(
                    "There is no Track at Position `%s` there are only" +
                            " `%s` Tracks in the Queue",
                    position,
                    audioPlayer.queue.size
                )
            )
        )
        return
    }

    event.reply(
        EmbedBuilder()
            .setDescription(
                String.format(
                    "**Removed [%s](%s)**",
                    audioTrack.songInfo.title,
                    audioTrack.songInfo.uri
                )
            )
            .setColor(Colours.godbotYellow)
            .build()
    )
}

fun loop(event: EventExtender, payload: SlashCommandPayload) {
    fun loopCheckParameter(event: SlashCommandEvent): Boolean {
        val mode: OptionMapping = event.getOption("mode") ?: throw ArgumentNotFoundException()
        return mode.asBoolean
    }

    val mode: Boolean
    try {
        mode = loopCheckParameter(event.event)
    } catch (e: ArgumentNotFoundException) {
        event.replyEphemeral(
            standardError(
                ErrorMessages.NOT_RECEIVED_PARAMETER
            )
        )
        return
    }

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

    if (player.loop == mode && player.loop) {
        event.replyEphemeral(standardError("The Player is already in Loop Mode"))
        return
    } else if (player.loop == mode) {
        event.replyEphemeral(standardError("Loop mode is already disabled"))
        return
    }

    player.loop = mode

    event.reply(
        EmbedBuilder()
            .setTitle(
                if (mode) {
                    "${EmojiIds.loop} Loop Mode Enabled"
                } else {
                    "${EmojiIds.noLoop} Loop Mode Disabled"
                }
            )
            .setColor(Colours.godbotYellow)
            .build()
    )
}

fun skipTo(event: EventExtender, payload: SlashCommandPayload) {
    fun skipCheckParameter(event: SlashCommandEvent): Long {
        val position: OptionMapping = event.getOption("position") ?: throw ArgumentNotFoundException()
        return position.asLong
    }

    val position: Long
    try {
        position = skipCheckParameter(event.event)
    } catch (e: ArgumentNotFoundException) {
        event.replyEphemeral(
            standardError(
                ErrorMessages.NOT_RECEIVED_PARAMETER
            )
        )
        return
    }

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
    if (player.queue.isEmpty()) {
        event.replyEphemeral(
            standardError(
                ErrorMessages.QUEUE_EMPTY
            )
        )
        return
    }

    try {
        player.skipTo((position - 1).toInt())
    } catch (e: IndexOutOfBoundsException) {
        event.replyEphemeral(
            standardError(
                "The Queue is `${player.queue.size}` big and the position is `$position`"
            )
        )
        return
    }

    if (player.currentTrack == null) {
        event.replyEphemeral(
            standardError(
                ErrorMessages.LOADING_FAILED
            )
        )
    }

    event.reply(
        EmbedBuilder()
            .setDescription("Skipped to `$position`, now playing " +
                    "[${player.currentTrack!!.songInfo.title}](${player.currentTrack!!.songInfo.uri})")
            .setColor(Colours.godbotYellow)
            .build()
    )
}

fun volume(event: EventExtender, payload: SlashCommandPayload) {
    fun volumeCheckParameters(event: SlashCommandEvent): Long {
        val level: OptionMapping = event.getOption("level") ?: throw ArgumentNotFoundException()
        return level.asLong
    }

    val level: Int

    try {
        level = volumeCheckParameters(event.event).toInt()
    } catch (e: CheckFailedException) {
        event.replyEphemeral(
            standardError(
                ErrorMessages.NOT_RECEIVED_PARAMETER
            )
        )
        return
    }

    val audioPlayer: AudioPlayerExtender

    try {
        audioPlayer = PlayerVault
            .getInstance()
            .getPlayer(
                JDAManager.getInstance().getJDA(payload.applicationId),
                payload.guild.id
            )
    } catch (e: PlayerNotFoundException) {
        event.replyEphemeral(
            standardError(ErrorMessages.NO_PLAYER_IN_VC)
        )
        return
    }

    val playerVolume: Int = audioPlayer.getVolume()
    var emojiId: String = EmojiIds.noAudioChange

    if (playerVolume > level*10) {
        emojiId = EmojiIds.quieter
    } else if (playerVolume < level*10) {
        emojiId = EmojiIds.louder
    }
    if (level == 0) {
        emojiId = EmojiIds.mute
    }

    audioPlayer.setVolume(level*10)

    event.reply(
        EmbedBuilder()
            .setDescription(
                String.format(
                    "%s **Set Volume to level to %s**",
                    emojiId,
                    level
                )
            )
            .setColor(Colours.godbotYellow)
            .build()
    )
}

fun seek(event: EventExtender, payload: SlashCommandPayload) {
    fun getSeekParameters(): List<Long> {
        val arr = ArrayList<Long>()
        val hours = event.getOption("hours")
        val minutes = event.getOption("minutes")
        val seconds = event.getOption("seconds")
        arr.add(hours?.asLong ?: 0)
        arr.add(minutes?.asLong ?: 0)
        arr.add(seconds?.asLong ?: 0)
        return arr
    }

    fun getSeekPoint(): Long {
        val hours = event.getOption("hours")
        val minutes = event.getOption("minutes")
        val seconds = event.getOption("seconds")
        return TimeUnit.HOURS.toMillis(hours?.asLong ?: 0)+
                TimeUnit.MINUTES.toMillis(minutes?.asLong ?: 0) +
                TimeUnit.SECONDS.toMillis(seconds?.asLong ?: 0)
    }

    val audioPlayer: AudioPlayerExtender

    try {
        audioPlayer = PlayerVault
            .getInstance()
            .getPlayer(
                JDAManager.getInstance().getJDA(payload.applicationId),
                payload.guild.id
            )
    } catch (e: GuildNotFoundException) {
        event.replyEphemeral(
            standardError(ErrorMessages.NO_PLAYER_IN_VC)
        )
        return
    } catch (e: JDANotFoundException) {
        event.replyEphemeral(
            standardError(ErrorMessages.NO_PLAYER_IN_VC)
        )
        return
    }

    if (audioPlayer.currentTrack == null) {
        event.replyEphemeral(
            standardError(ErrorMessages.NO_PLAYING_TRACK)
        )
        return
    }

    val seekPoint: Long = getSeekPoint()
    val duration: Long = audioPlayer.currentTrack?.songInfo?.duration ?: 0
    println(duration)
    println(seekPoint)

    if (seekPoint > duration) {
        event.replyEphemeral(
            standardError("I can't skip to ${DurationCalc.longToString(seekPoint)}" +
                    ", because the song is only ${DurationCalc.longToString(duration)} long")
        )
        return
    }

    audioPlayer.seek(seekPoint)

    event.reply(
        EmbedBuilder()
            .setTitle(audioPlayer.currentTrack?.songInfo?.title)
            .setThumbnail(audioPlayer.currentTrack?.songInfo?.thumbnailUri)
            .setDescription(
                trackLines(seekPoint, audioPlayer.getCurrentSongDuration()) + " "
                        + "**${DurationCalc.longToString(seekPoint)} - ${DurationCalc.longToString(duration)}**"
            )
            .setColor(Colours.godbotHeavenYellow)
            .build()
    )
}
