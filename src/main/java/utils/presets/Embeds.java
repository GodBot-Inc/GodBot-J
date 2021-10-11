package utils.presets;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;

import java.awt.*;

public class Embeds {
    public static MessageEmbed error(String description) {
        return new EmbedBuilder().
                setDescription(description).
                setColor(Color.RED).
                build();
    }
}
