package utils.presets;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;

import java.awt.*;

public class Embeds {
    public static MessageEmbed error(String description) {
        return new EmbedBuilder().
                setDescription("<:godbotWarning:897386354567180369> " + description).
                setColor(Color.GRAY).
                build();
    }

    public static MessageEmbed playTrack(AudioTrack audioTrack) {
        System.out.println("Play Track preset called");
        EmbedBuilder eb = new EmbedBuilder();
        eb.setDescription(
                String.format(
                        "Now playing [%s](%s)",
                        audioTrack.getInfo().title,
                        audioTrack.getInfo().uri
                )
        );
        eb.setColor(Color.YELLOW);
        return eb.build();
    }
}
