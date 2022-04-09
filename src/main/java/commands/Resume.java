package commands;

import ktLogging.UtilsKt;
import ktLogging.custom.GodBotChildLogger;
import ktLogging.custom.GodBotLogger;
import ktUtils.*;
import net.dv8tion.jda.api.EmbedBuilder;
import singeltons.JDAManager;
import singeltons.PlayerVault;
import snippets.Colours;
import snippets.EmojiIds;
import snippets.ErrorMessages;
import utils.EventExtender;

public class Resume implements Command {

    public static void trigger(EventExtender event, SlashCommandPayload payload) {
        GodBotChildLogger logger = new GodBotLogger().command(
                "Resume",
                UtilsKt.formatPayload(payload)
        );

        AudioPlayerExtender player = PlayerVault
                    .getInstance()
                    .getPlayer(
                            JDAManager.getInstance().getJDA(applicationId),
                            payload.getGuild().getId()
                    );
        if (player == null) {
            ErrorHandlerKt.handleDefaultErrorResponse(event, payload, ErrorMessages.NO_PLAYER_FOUND, logger);
            return;
        }

        logger.info("Got AudioPlayer");

        if (!player.getVoiceChannel().getId().equals(payload.getVoiceChannel().getId())) {
            ErrorHandlerKt.handleDefaultErrorResponse(event, payload, ErrorMessages.NO_PLAYER_IN_VC);
            return;
        }

        if (player.getCurrentTrack() == null) {
            ErrorHandlerKt.handleDefaultErrorResponse(event, payload, ErrorMessages.NO_PLAYING_TRACK);
            return;
        }
        if (!player.isPaused()) {
            ErrorHandlerKt.handleDefaultErrorResponse(event, payload, "Player is not paused", logger);
            return;
        }

        player.setPaused(false);
        event.reply(
                new EmbedBuilder()
                        .setDescription(
                                String.format(
                                        "%s **Player resumed**",
                                        EmojiIds.play
                                )
                        )
                        .setColor(Colours.godbotYellow)
                        .build()
        );
        logger.info("Response sent");
    }
}
