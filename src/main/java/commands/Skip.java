package commands;

import ktLogging.UtilsKt;
import ktLogging.custom.GodBotChildLogger;
import ktLogging.custom.GodBotLogger;
import ktUtils.ErrorHandlerKt;
import ktUtils.QueueEmptyException;
import net.dv8tion.jda.api.EmbedBuilder;
import objects.AudioPlayerExtender;
import objects.AudioTrackExtender;
import objects.EventFacade;
import objects.SlashCommandPayload;
import org.jetbrains.annotations.NotNull;
import singeltons.JDAManager;
import singeltons.PlayerVault;
import snippets.Colours;
import snippets.EmojiIds;
import snippets.ErrorMessages;

public class Skip implements Command {

    public static void trigger(@NotNull EventFacade event, SlashCommandPayload payload) {
        GodBotChildLogger logger = new GodBotLogger().command(
                "Skip",
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

        AudioTrackExtender audioTrack;
        try {
            audioTrack = player.playNext();
        } catch (QueueEmptyException e) {
            ErrorHandlerKt.handleDefaultErrorResponse(event, payload, ErrorMessages.QUEUE_EMPTY, logger);
            return;
        }

        event.reply(
                new EmbedBuilder()
                        .setDescription(
                                String.format(
                                        "%s **Skipped Song, Now Playing: [%s](%s)**",
                                        EmojiIds.nextTrack,
                                        audioTrack.getSongInfo().getTitle(),
                                        audioTrack.getSongInfo().getUri()
                                )
                        )
                        .setColor(Colours.godbotYellow)
                        .build()
        );
        logger.info("Response sent");
    }
}
