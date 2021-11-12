package discord.audio.lavaplayer;


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
        // Here we allocate 400 bits to the ByteBuffer which is the maximum number of bits he can send every 20ms
        // If it wants to send more than that we get a BufferOverflow Error and nothing will be played
        this.buffer = ByteBuffer.allocate(400);
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
