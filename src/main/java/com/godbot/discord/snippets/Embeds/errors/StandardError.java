package com.godbot.discord.snippets.Embeds.errors;

import com.godbot.discord.snippets.Embeds.Colours;
import com.godbot.utils.discord.EmojiIds;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;

public class StandardError {
    public static MessageEmbed build(String description) {
        return new EmbedBuilder()
                .setTitle(
                        String.format(
                                "%s %s",
                                EmojiIds.godbotWarning,
                                description
                        )
                )
//                .setColor(Color.orange)
                .setColor(Colours.godbotYellow)
                .build();
    }
}
