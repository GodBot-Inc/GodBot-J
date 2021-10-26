package discord.listeners;

import discord.commands.music.Play;
import discord.snippets.Embeds.errors.StandardError;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import utils.logging.CommandLogger;
import utils.logging.LoggerContent;

import javax.annotation.Nonnull;
import java.util.HashMap;

public class InteractionListener extends ListenerAdapter {

    private final CommandLogger logger;

    public InteractionListener() {
        this.logger = CommandLogger.getInstance();
    }

    public HashMap<String, String> getLogArgs(@Nonnull SlashCommandEvent event) {
        User user = event.getUser();
        Guild guild = event.getGuild();
        if (guild == null) {
            return new HashMap<String, String>() {{
               put("GuildId", "null");
               put("AuthorId", user.getId());
               put("GuildName", "null");
               put("AuthorName", user.getName() + user.getDiscriminator());
            }};
        }
        return new HashMap<String, String>() {{
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
            case "play":
                OptionMapping urlParameter = event.getOption("url");
                if (urlParameter == null) {
                    event
                            .replyEmbeds(StandardError.build("You did not pass an url as parameter"))
                            .setEphemeral(true)
                            .queue();
                    return;
                }
                Play.trigger(event, urlParameter.getAsString());
                break;
        }
    }
}
