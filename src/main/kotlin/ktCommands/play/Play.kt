package ktCommands.play

import commands.Command
import constants.songNotFound
import constants.songProcessingError
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import ktCommands.play.lib.InteractionHookWrapper
import ktCommands.play.services.getYTPlaylistInfo
import ktCommands.play.services.getYTVideoInfo
import ktCommands.play.utils.convertYtUrlToId
import ktCommands.play.utils.isSong
import ktCommands.play.utils.isValid
import ktSnippets.playVideo
import ktUtils.CouldNotExtractVideoInformation
import ktUtils.TrackNotFoundException
import ktUtils.VideoNotFoundException
import lib.EventExtender
import lib.SlashCommandPayload
import playableInfo.YouTubePlaylist
import playableInfo.YouTubeSong
import singeltons.AudioPlayerManagerWrapper
import singeltons.JDAManager
import snippets.ErrorMessages

suspend fun play(event: EventExtender, payload: SlashCommandPayload) {
    val url = event.getOption("url")?.asString
    if (url == null) {
        event.error(ErrorMessages.NOT_RECEIVED_PARAMETER)
        return
    }
    if (!isValid(url)) {
        event.error(ErrorMessages.INVALID_URL)
        return
    }

    coroutineScope {
        val isSong = isSong(url)
        if (isSong == null) {
            event.error(ErrorMessages.INVALID_PLATFORM)
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
            JDAManager.getInstance().getJDA(Command.applicationId),
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
    event: EventExtender,
    payload: SlashCommandPayload,
    hook: InteractionHookWrapper,
    url: String
) = coroutineScope {
    val infoJob: Deferred<YouTubePlaylist> = async { getYTPlaylistInfo(convertYtUrlToId(url)) }

    val player = AudioPlayerManagerWrapper
        .getInstance()
        .getOrCreatePlayer(
            JDAManager.getInstance().getJDA(Command.applicationId),
            payload.guild.id,
            payload.voiceChannel
        )
    player.openConnection()
}