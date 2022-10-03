package commands

import commands.play.utils.isSong
import commands.play.utils.isValid
import commands.play.utils.playPlaylistMessage
import commands.play.utils.playVideoMessage
import constants.*
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.runBlocking
import lib.jda.EventFacade
import lib.jda.InteractionHookWrapper
import lib.lavaplayer.PlayerManager
import objects.SlashCommandPayload
import objects.playableInformation.YouTubePlaylist
import objects.playableInformation.YouTubeSong
import services.getYTPlaylistInfo
import services.getYTVideoInfoFromUrl
import utils.CouldNotExtractVideoInformation
import utils.NotFoundException
import utils.VideoNotFoundException
import utils.YouTubeApiException

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

    val isSong = isSong(url)
    if (isSong == null) {
        event.error(invalidPlatform)
        return
    }

    val hook = InteractionHookWrapper(event.getHook())
    event.deferReply()

    if (isSong)
        resolveVideo(payload, hook, url)
    else
        resolvePlaylist(event, payload, hook, url)
}

suspend fun resolveVideo(
    payload: SlashCommandPayload,
    hook: InteractionHookWrapper,
    url: String
) = coroutineScope {
    val infoJob: Deferred<YouTubeSong> = async { getYTVideoInfoFromUrl(url) }

    val player = PlayerManager.getOrCreatePlayer(
        payload.guild,
        payload.voiceChannel
    )

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
    } catch (e: NotFoundException) {
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

    val player = PlayerManager.getOrCreatePlayer(
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

    if (event.getOption("shuffle")?.asBoolean == true)
        info.songInfos.shuffle()

    val successfulSongs = runBlocking { player.playPlaylist(info.songInfos, payload) }

    if (successfulSongs == 0) {
        hook.error(loadingPlaylistFailed)
        return@coroutineScope
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
