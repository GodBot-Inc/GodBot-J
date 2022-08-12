package commands.play

import commands.play.lib.InteractionHookWrapper
import commands.play.services.getYTPlaylistInfo
import commands.play.services.getYTVideoInfoFromUrl
import commands.play.utils.isSong
import commands.play.utils.isValid
import commands.play.utils.playPlaylistMessage
import commands.play.utils.playVideoMessage
import constants.*
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import lib.lavaplayer.PremiumPlayerManager
import objects.EventFacade
import objects.SlashCommandPayload
import objects.playableInformation.YouTubePlaylist
import objects.playableInformation.YouTubeSong
import utils.*

suspend fun play(event: EventFacade, payload: SlashCommandPayload) {
    val url = event.getOption("url")?.asString
    if (url == null) {
        event.error(notReceivedParameter)
        return
    }
    if (!isValid(url)) {
        event.error(invalidURL)
        return
    }

    coroutineScope {
        val isSong = isSong(url)
        if (isSong == null) {
            event.error(invalidPlatform)
            return@coroutineScope
        }

        val hook = InteractionHookWrapper(event.getHook())
        event.deferReply()

        if (isSong)
            resolveVideo(payload, hook, url)
        else
            resolvePlaylist(event, payload, hook, url)
    }
}

suspend fun resolveVideo(
    payload: SlashCommandPayload,
    hook: InteractionHookWrapper,
    url: String
) = coroutineScope {
    val infoJob: Deferred<YouTubeSong> = async { getYTVideoInfoFromUrl(url) }

    val player = PremiumPlayerManager.getOrCreatePlayer(
        payload.guild,
        payload.voiceChannel
    )
    player.openConnection()

    val info: YouTubeSong
    try {
        info = infoJob.await()
    } catch (e: VideoNotFoundException) {
        hook.error(songNotFound)
        return@coroutineScope
    } catch (e: CouldNotExtractVideoInformation) {
        hook.error(songProcessingError)
        return@coroutineScope
    }

    val position: Int
    try {
        position = player.play(info, payload)
    } catch (e: TrackNotFoundException) {
        hook.error(songNotFound)
        return@coroutineScope
    }

    hook.reply(
        playVideoMessage(
        payload.member,
        info,
        position,
        player.queue.size + 1
        )
    )
}

suspend fun resolvePlaylist(
    event: EventFacade,
    payload: SlashCommandPayload,
    hook: InteractionHookWrapper,
    url: String
) = coroutineScope {
    val infoJob: Deferred<YouTubePlaylist> = async { getYTPlaylistInfo(url) }

    val player = PremiumPlayerManager.getOrCreatePlayer(
        payload.guild,
        payload.voiceChannel
    )

    val positionInQueue = player.queue.size + 1
    val info: YouTubePlaylist
    try {
        info = infoJob.await()
    } catch (e: YouTubeApiException) {
        hook.error(loadingPlaylistFailed)
        e.printStackTrace()
        return@coroutineScope
    }

    launch {
        player.openConnection()
        if (event.getOption("shuffle")?.asBoolean == true)
            info.songInfos.shuffle()

        for (songInfo in info.songInfos) {
            try {
                /*
                Chance to optimize:
                    just create a method, which loads the playable Song, and store the Deferred responses in an array
                    (same as in YT Api) just load them in, as soon as every song is loaded inside the Queue.
                    Only do that, if you really need to improve the loading speeds of songs, since it adds more code
                    complexity.
                 */
                player.play(songInfo, payload)
            } catch (ignore: NotFoundException) { }
        }
    }

    hook.reply(
        playPlaylistMessage(
            payload.member,
            info,
            positionInQueue,
             positionInQueue + info.videoIds.size - 1
        )
    )
}