package commands;

import ktLogging.UtilsKt;
import ktLogging.custom.GodBotChildLogger;
import ktLogging.custom.GodBotLogger;
import ktUtils.AudioPlayerExtender;
import ktUtils.ErrorHandlerKt;
import ktUtils.SlashCommandPayload;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import org.jetbrains.annotations.NotNull;
import singeltons.AudioPlayerManagerWrapper;
import singeltons.JDAManager;
import snippets.Colours;
import snippets.ErrorMessages;
import utils.EventExtender;

public class Join implements Command {

    public static void trigger(@NotNull EventExtender event, SlashCommandPayload payload) {
        GodBotChildLogger logger = new GodBotLogger().command(
                "Join",
                UtilsKt.formatPayload(payload)
        );
        JDA godbotJDA = JDAManager.getInstance().getJDA(applicationId);
        if (godbotJDA == null) {
            ErrorHandlerKt.handleDefaultErrorResponse(event, payload, ErrorMessages.GENERAL_ERROR, logger);
            return;
        }

        AudioPlayerExtender audioPlayer;
        audioPlayer = AudioPlayerManagerWrapper
                .getInstance()
                .getOrCreatePlayer(godbotJDA, payload.getGuild().getId(), payload.getVoiceChannel());
        logger.info("Got Audio Player");

        if (!audioPlayer.getVoiceChannel().equals(payload.getVoiceChannel())) {
            ErrorHandlerKt.handleDefaultErrorResponse(event, payload, ErrorMessages.NO_PLAYER_IN_VC, logger);
            return;
        }

        if (audioPlayer.isConnected()) {
            ErrorHandlerKt.handleDefaultErrorResponse(event, payload, "I'm already connected to your channel :thinking:", logger);
            return;
        }

        audioPlayer.openConnection();
        logger.info("Successfully Connected to Channel " + audioPlayer.getVoiceChannel().getName());

        event.reply(
                new EmbedBuilder()
                        .setTitle(
                                String.format(
                                        "Joined the Channel %s",
                                        payload.getVoiceChannel().getName()
                                )
                        )
                        .setColor(Colours.godbotYellow)
                        .build()
        );
    }
}
