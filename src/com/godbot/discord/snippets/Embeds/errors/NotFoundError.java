package discord.snippets.Embeds.errors;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;

import java.awt.*;

public class NotFoundError {
    public static MessageEmbed build(String description) {
        return new EmbedBuilder()
                .setDescription(
                        String.format(
                                "<:noTracksFound:897394939586043934> %s",
                                description
                        )
                )
                .setColor(Color.RED)
                .build();
    }
}
