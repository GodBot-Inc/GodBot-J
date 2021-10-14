package discord.snippets.Embeds.trackInfo;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;

import java.awt.*;

public class PlayTrack {
    public static MessageEmbed build(String title, String author, String uri) {
        return new EmbedBuilder()
                .setDescription(
                        String.format(
                                "Now playing [%s](%s)",
                                title,
                                uri
                        )
                )
                .setColor(Color.ORANGE)
                .addField("Author", author, false)
                .build();
    }
}
