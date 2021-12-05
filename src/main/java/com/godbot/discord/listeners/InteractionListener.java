package com.godbot.discord.listeners;

import com.godbot.discord.commands.music.Play;
import com.godbot.discord.commands.musicControl.Pause;
import com.godbot.discord.commands.musicControl.Resume;
import com.godbot.discord.commands.musicControl.Stop;
import com.godbot.discord.snippets.Embeds.errors.StandardError;
import com.godbot.utils.logging.ListenerLogger;
import com.godbot.utils.logging.LoggerContent;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;

import javax.annotation.Nonnull;
import java.util.HashMap;

public class InteractionListener extends ListenerAdapter {

    private final ListenerLogger logger;

    public InteractionListener() {
        this.logger = new ListenerLogger("InteractionListener");
    }

    public HashMap<String, String> getLogArgs(@Nonnull SlashCommandEvent event) {
        User user = event.getUser();
        Guild guild = event.getGuild();
        if (guild == null) {
            return new HashMap<>() {{
                put("GuildId", "null");
                put("AuthorId", user.getId());
                put("GuildName", "null");
                put("AuthorName", user.getName() + user.getDiscriminator());
            }};
        }
        return new HashMap<>() {{
            put("GuildId", guild.getId());
            put("AuthorId", user.getId());
            put("GuildName", guild.getName());
            put("AuthorName", user.getName() + user.getDiscriminator());
        }};
    }

    public void onSlashCommand(@Nonnull SlashCommandEvent event) {
        this.logger.info(
                new LoggerContent(
                        "info",
                        "SlashCommandEvent",
                        "",
                        getLogArgs(event)
                )
        );
        if (event.getGuild() == null) { return; }
        switch (event.getName()) {
            case "play" -> {
                OptionMapping urlParameter = event.getOption("url");
                if (urlParameter == null) {
                    event
                            .replyEmbeds(
                                    StandardError.build(
                                            "You did not pass an url as parameter"
                                    )
                            )
                            .setEphemeral(true)
                            .queue();
                    return;
                }
                Play.trigger(event);
            }
            case "pause" -> Pause.trigger(event);
            case "resume" -> Resume.trigger(event);
            case "stop" -> Stop.trigger(event);
        }
    }
}
