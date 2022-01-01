package utils;

import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;

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
}
