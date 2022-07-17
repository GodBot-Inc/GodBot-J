package utils;

import ktSnippets.ErrorsKt;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;

public class EventExtender {

    public final SlashCommandEvent event;

    public EventExtender(SlashCommandEvent event) {
        this.event = event;
    }

    public void replyEphemeral(String message) {
        event
                .reply(message)
                .setEphemeral(true)
                .queue();
    }

    public void replyEphemeral(MessageEmbed embed) {
        event
                .replyEmbeds(embed)
                .setEphemeral(true)
                .queue();
    }

    public void reply(String message) {
        event
                .reply(message)
                .queue();
    }

    public void reply(MessageEmbed embed) {
        event
                .replyEmbeds(embed)
                .queue();
    }

    public void error(String message) {
        event
                .replyEmbeds(ErrorsKt.standardError(message))
                .setEphemeral(true)
                .queue();
    }

    public void clearError(String message) {
        event
                .replyEmbeds(ErrorsKt.standardError(message))
                .queue();
    }

    public OptionMapping getOption(String optName) {
        return event.getOption(optName);
    }
}
