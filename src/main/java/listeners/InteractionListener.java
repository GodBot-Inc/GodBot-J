package listeners;

import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import utils.loggers.ListenerLogger;

import java.util.concurrent.TimeUnit;

import javax.annotation.Nonnull;

public class InteractionListener extends ListenerAdapter {

    ListenerLogger logger;

    public InteractionListener(ListenerLogger logger) {
        this.logger = logger;
    }

    public void onSlashCommand(@Nonnull SlashCommandEvent event) {
        /* TODO: event.reply().setEphemeral(true).queue(); used if only the user who used the slash command should see
            the response message (in case of an error)
         */
        event.deferReply().queue();
        if (event.getGuild() == null) { return; }
        switch (event.getName()) {
            case "play":
                logger.log("Play slash command triggered");
                try {
                    TimeUnit.SECONDS.sleep(2);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                event.reply("Hello").setEphemeral(true).queue();
                break;

            case "pause":
                logger.log("Pause slash command triggered");
                try {
                    TimeUnit.SECONDS.sleep(5);
                } catch (InterruptedException e) {
                    logger.warn("Error: " + e);
                }
                event.reply("moin").queue();
                break;
        }
    }
}
