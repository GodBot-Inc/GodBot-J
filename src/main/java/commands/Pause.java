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

public class Pause implements Command {

    public static void trigger(EventExtender event, SlashCommandPayload payload) {
        GodBotChildLogger logger = new GodBotLogger().command(
                "Pause",
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

        if (!player.getVoiceChannel().getId().equals(payload.getVoiceChannel().getId())) {
            ErrorHandlerKt.handleDefaultErrorResponse(event, payload, ErrorMessages.NO_PLAYER_IN_VC, logger);
            return;
        }

        if (player.getCurrentTrack() == null) {
            ErrorHandlerKt.handleDefaultErrorResponse(event, payload, ErrorMessages.NO_PLAYING_TRACK, logger);
            return;
        }
        if (player.isPaused()) {
            ErrorHandlerKt.handleDefaultErrorResponse(event, payload, "Player is already paused", logger);
            return;
        }

        player.setPaused(true);
        logger.info("Successfully paused player");
        event.reply(
                new EmbedBuilder()
                        .setDescription(
                                String.format(
                                        "%s **Player paused**",
                                        EmojiIds.pause
                                )
                        )
                        .setColor(Colours.godbotYellow)
                        .build()
        );
    }
}
