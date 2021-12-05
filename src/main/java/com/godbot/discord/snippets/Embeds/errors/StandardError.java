package com.godbot.discord.snippets.Embeds.errors;

import com.godbot.utils.discord.EmojiIds;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;

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
