package commands;

import ktLogging.UtilsKt;
import ktLogging.custom.GodBotChildLogger;
import ktLogging.custom.GodBotLogger;
import ktUtils.*;
import lib.AudioPlayerExtender;
import lib.SlashCommandPayload;
import net.dv8tion.jda.api.EmbedBuilder;
import singeltons.JDAManager;
import singeltons.PlayerVault;
import snippets.Colours;
import snippets.EmojiIds;
import snippets.ErrorMessages;
import lib.EventExtender;

public class Stop implements Command {

    public static void trigger(EventExtender event, SlashCommandPayload payload) {
        GodBotChildLogger logger = new GodBotLogger().command(
                "Stop",
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
            ErrorHandlerKt.handleDefaultErrorResponse(event, payload, ErrorMessages.NO_PLAYER_IN_VC, logger);
            return;
        }

        if (player.getCurrentTrack() == null) {
            ErrorHandlerKt.handleDefaultErrorResponse(event, payload, ErrorMessages.NO_PLAYING_TRACK, logger);
            return;
        }

        player.stop();
        event.reply(
                new EmbedBuilder()
                        .setDescription(
                                String.format(
                                        "%s **Player stopped**",
                                        EmojiIds.stop
                                )
                        )
                        .setColor(Colours.godbotYellow)
                        .build()
        );
        logger.info("Response sent");
    }
}
