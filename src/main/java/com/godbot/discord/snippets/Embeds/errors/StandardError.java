package discord.snippets.Embeds.errors;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import utils.discord.EmojiIds;

import java.awt.*;

public class StandardError {
    public static MessageEmbed build(String description) {
        return new EmbedBuilder()
                .setDescription(
                        String.format(
                                "%s %s",
                                EmojiIds.godbotWarning,
                                description
                        )
                )
                .setColor(Color.RED)
                .build();
    }
}
