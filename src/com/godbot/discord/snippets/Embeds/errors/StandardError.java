package discord.snippets.Embeds.errors;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;

import java.awt.*;

public class StandardError {
    public static MessageEmbed build(String description) {
        return new EmbedBuilder()
                .setDescription("<:godbotWarning:897386354567180369> " + description)
                .setColor(Color.RED)
                .build();
    }
}
