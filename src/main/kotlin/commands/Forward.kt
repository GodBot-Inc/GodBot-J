package commands

import constants.secondary
import commands.play.utils.trackLines
import utils.getPlayingPlayer
import utils.millisToString
import net.dv8tion.jda.api.EmbedBuilder
import lib.jda.EventFacade
import objects.SlashCommandPayload
import java.util.concurrent.TimeUnit

fun forward(event: EventFacade, payload: SlashCommandPayload) {
    fun getSeekPoint(): Long {
        val hours = event.getOption("hours")
        val minutes = event.getOption("minutes")
        val seconds = event.getOption("seconds")
        return TimeUnit.HOURS.toMillis(hours?.asLong ?: 0)+
                TimeUnit.MINUTES.toMillis(minutes?.asLong ?: 0) +
                TimeUnit.SECONDS.toMillis(seconds?.asLong ?: 0)
    }

    val player = getPlayingPlayer(
        payload.guild.id,
        payload.voiceChannel.id,
        event
    ) ?: return

    val seekPoint: Long = getSeekPoint()
    val duration: Long = player.currentTrack?.songInfo?.duration ?: 0

    if (seekPoint > duration) {
        event.error("I can't skip to ${millisToString(seekPoint)}" +
                ", because the song is only ${millisToString(duration)} long")
        return
    }

    player.seek(seekPoint)

    event.reply(
        EmbedBuilder()
            .setTitle(player.currentTrack?.songInfo?.title)
            .setThumbnail(player.currentTrack?.songInfo?.thumbnailUri)
            .setDescription(
                trackLines(seekPoint, player.getCurrentSongDuration()) + " "
                        + "**${millisToString(seekPoint)} - ${millisToString(duration)}**"
            )
            .setColor(secondary)
            .build()
    )
}
