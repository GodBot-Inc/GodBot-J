package utils.discord;

import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.Interaction;

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
}
