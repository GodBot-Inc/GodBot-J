package lavaplayerHandlers;


import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.track.playback.MutableAudioFrame;
import net.dv8tion.jda.api.audio.AudioSendHandler;

import java.nio.ByteBuffer;

public class AudioPlayerSendHandler implements AudioSendHandler {
    private final AudioPlayer audioPlayer;
    private final ByteBuffer buffer;
    private final MutableAudioFrame frame;
//    private AudioFrame lastFrame;

    public AudioPlayerSendHandler(AudioPlayer audioPlayer) {
        this.audioPlayer = audioPlayer;

        // Maximum data package, which can be sent: 349568 bytes -> 8192 kb/s
        this.buffer = ByteBuffer.allocate(349568);
        this.frame = new MutableAudioFrame();
        this.frame.setBuffer(buffer);
    }

    @Override
    public boolean canProvide() {
        return this.audioPlayer.provide(this.frame);
    }
//    public boolean canProvide() {
//        lastFrame = audioPlayer.provide();
//        return lastFrame != null;
//    }

    @Override
//    public ByteBuffer provide20MsAudio() {
//        return ByteBuffer.wrap(lastFrame.getData());
//    }

    public ByteBuffer provide20MsAudio() {
        return this.buffer.flip();
    }

    @Override
    public boolean isOpus() {
        return true;
    }
}
