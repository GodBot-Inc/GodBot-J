package discord.audio.lavaplayer;


import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.track.playback.AudioFrame;
import com.sedmelluq.discord.lavaplayer.track.playback.MutableAudioFrame;
import net.dv8tion.jda.api.audio.AudioSendHandler;

import java.nio.ByteBuffer;

public class AudioPlayerSendHandler implements AudioSendHandler {
//    private final AudioPlayer audioPlayer;
//    private final ByteBuffer buffer;
//    private final MutableAudioFrame frame;
//
//    public AudioPlayerSendHandler(AudioPlayer audioPlayer) {
//        this.audioPlayer = audioPlayer;
//        this.buffer = ByteBuffer.allocate(1024);
//        this.frame = new MutableAudioFrame();
//        this.frame.setBuffer(buffer);
//    }
//
//    @Override
//    public boolean canProvide() {
//        return this.audioPlayer.provide(this.frame);
//    }
//
//    @Override
//    public ByteBuffer provide20MsAudio() {
//        return this.buffer.flip();
//    }
//
//    @Override
//    public boolean isOpus() {
//        return true;
//    }
    private final AudioPlayer audioPlayer;
    private AudioFrame lastFrame;

    public AudioPlayerSendHandler(AudioPlayer audioPlayer) {
        this.audioPlayer = audioPlayer;
    }

    @Override
    public boolean canProvide() {
        lastFrame = audioPlayer.provide();
        return lastFrame != null;
    }

    @Override
    public ByteBuffer provide20MsAudio() {
        return ByteBuffer.wrap(lastFrame.getData());
    }

    @Override
    public boolean isOpus() {
        return true;
    }
}