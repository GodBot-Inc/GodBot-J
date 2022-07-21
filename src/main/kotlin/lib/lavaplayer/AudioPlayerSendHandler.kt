package lib.lavaplayer

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer
import com.sedmelluq.discord.lavaplayer.track.playback.MutableAudioFrame
import net.dv8tion.jda.api.audio.AudioSendHandler
import java.nio.ByteBuffer

class AudioPlayerSendHandler(val audioPlayer: AudioPlayer): AudioSendHandler {

    private val buffer: ByteBuffer = ByteBuffer.allocate(1024)
    private val frame: MutableAudioFrame = MutableAudioFrame()

    init {
        this.frame.setBuffer(this.buffer)
    }

    override fun canProvide(): Boolean = audioPlayer.provide(frame)

    override fun provide20MsAudio(): ByteBuffer {
        buffer.flip()
        return buffer
    }

    override fun isOpus(): Boolean = true
}