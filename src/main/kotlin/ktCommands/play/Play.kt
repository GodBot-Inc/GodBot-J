package ktCommands.play

import constants.*
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import ktCommands.play.lib.InteractionHookWrapper
import ktCommands.play.services.getYTPlaylistInfo
import ktCommands.play.services.getYTVideoInfo
import ktCommands.play.utils.convertYtUrlToId
import ktCommands.play.utils.isSong
import ktCommands.play.utils.isValid
import ktSnippets.playPlaylistMessage
import ktSnippets.playVideoMessage
import ktUtils.CouldNotExtractVideoInformation
import ktUtils.TrackNotFoundException
import ktUtils.VideoNotFoundException
import ktUtils.YouTubeApiException
import lib.jda.PremiumPlayerManager
import objects.EventFacade
import objects.SlashCommandPayload
import objects.playableInformation.YouTubePlaylist
import objects.playableInformation.YouTubeSong

suspend fun play(event: EventFacade, payload: SlashCommandPayload) {
    println("Play called")
    val url = event.getOption("url")?.asString
    if (url == null) {
        event.error(notReceivedParameter)
        return
    }
    if (!isValid(url)) {
        event.error(invalidURL)
        return
    }
    println("Entering coroutine")

    coroutineScope {
        val isSong = isSong(url)
        if (isSong == null) {
            event.error(invalidPlatform)
            return@coroutineScope
        }

        val hook = InteractionHookWrapper(event.getHook())
        event.deferReply()
        println("defered")

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
    val infoJob: Deferred<YouTubeSong> = async { getYTVideoInfo(convertYtUrlToId(url)) }

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

    hook.reply(playVideoMessage(
        payload.member,
        info,
        position,
        player.queue.size + 1
    ))
}

suspend fun resolvePlaylist(
    event: EventFacade,
    payload: SlashCommandPayload,
    hook: InteractionHookWrapper,
    url: String
) = coroutineScope {
    println("Called ResolvePlaylist")
    val infoJob: Deferred<YouTubePlaylist> = async { getYTPlaylistInfo(convertYtUrlToId(url)) }

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
                player.play(info, payload)
            } catch (ignore: TrackNotFoundException) { }
        }
    }

    println("Replying")
    hook.reply(
        playPlaylistMessage(
            payload.member,
            info,
            positionInQueue,
             positionInQueue + info.videoIds.size - 1
        )
    )
}