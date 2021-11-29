package discord.snippets.Embeds.errors;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import utils.discord.Emojis;

import java.awt.*;

public class EmptyError {
    public static MessageEmbed build(String description) {
        return new EmbedBuilder()
                .setDescription(
                        String.format(
                                "%s %s",
                                Emojis.NotFound2,
                                description
                        )
                )
                .setColor(Color.RED)
                .build();
    }
}
