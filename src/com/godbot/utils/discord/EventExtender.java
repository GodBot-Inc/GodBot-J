package utils.discord;

import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.Interaction;

public class EventExtender {

    private final SlashCommandEvent event;

    public EventExtender(SlashCommandEvent event) {
        this.event = event;
    }

    public void sendEphermal(String message) {
        event
                .reply(message)
                .setEphemeral(true)
                .queue();
    }

    public void sendEphermal(MessageEmbed embed) {
        event
                .replyEmbeds(embed)
                .setEphemeral(true)
                .queue();
    }
}
