package discord.audio.lavaplayer;


import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.track.playback.AudioFrame;
import com.sedmelluq.discord.lavaplayer.track.playback.MutableAudioFrame;
import net.dv8tion.jda.api.audio.AudioSendHandler;

import java.nio.Buffer;
import java.nio.ByteBuffer;

public class AudioPlayerSendHandler implements AudioSendHandler {
    // TODO For this version see Youtube/Später Ansehen
    private final AudioPlayer audioPlayer;
    private final ByteBuffer buffer;
    private final MutableAudioFrame frame;

    public AudioPlayerSendHandler(AudioPlayer audioPlayer) {
        this.audioPlayer = audioPlayer;
        // ByteBuffer.allocate(amount) - of bytes that the player can send (audio quality)
        // Here we allocate 524 bytes to the player
        this.buffer = ByteBuffer.allocate(4192);
        this.frame = new MutableAudioFrame();
        this.frame.setBuffer(buffer);
    }

    @Override
    public boolean canProvide() {
        return this.audioPlayer.provide(this.frame);
    }

    @Override
    public ByteBuffer provide20MsAudio() {
        final Buffer tmp = ((Buffer) this.buffer).flip();

        return (ByteBuffer) tmp;

        // Java version is too low
//        return this.buffer.flip();
    }
//    private final AudioPlayer audioPlayer;
//    private AudioFrame lastFrame;
//
//    public AudioPlayerSendHandler(AudioPlayer audioPlayer) {
//        this.audioPlayer = audioPlayer;
//    }
//
//    @Override
//    public boolean canProvide() {
//        lastFrame = audioPlayer.provide();
//        return lastFrame != null;
//    }
//
//    @Override
//    public ByteBuffer provide20MsAudio() {
//        return ByteBuffer.wrap(lastFrame.getData());
//    }

    @Override
    public boolean isOpus() {
        return true;
    }
}
