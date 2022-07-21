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
import ktSnippets.playPlaylist
import ktSnippets.playVideo
import ktUtils.CouldNotExtractVideoInformation
import ktUtils.TrackNotFoundException
import ktUtils.VideoNotFoundException
import objects.EventFacade
import objects.SlashCommandPayload
import playableInfo.YouTubePlaylist
import playableInfo.YouTubeSong
import singeltons.AudioPlayerManagerWrapper
import singeltons.JDAManager

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
    val infoJob: Deferred<YouTubeSong> = async { getYTVideoInfo(convertYtUrlToId(url)) }

    val player = AudioPlayerManagerWrapper
        .getInstance()
        .getOrCreatePlayer(
            JDAManager.getInstance().getJDA(payload.applicationId),
            payload.guild.id,
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

    hook.reply(playVideo(
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
    val infoJob: Deferred<YouTubePlaylist> = async { getYTPlaylistInfo(convertYtUrlToId(url)) }

    val player = AudioPlayerManagerWrapper
        .getInstance()
        .getOrCreatePlayer(
            JDAManager.getInstance().getJDA(payload.applicationId),
            payload.guild.id,
            payload.voiceChannel
        )

    val positionInQueue = player.queue.size + 1
    val info = infoJob.await()

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

    hook.reply(
        playPlaylist(
            payload.member,
            info,
            positionInQueue,
             positionInQueue + info.videoIds.size - 1
        )
    )
}