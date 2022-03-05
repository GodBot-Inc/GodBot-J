package commands;

import ktSnippets.ErrorsKt;
import ktUtils.*;
import net.dv8tion.jda.api.EmbedBuilder;
import org.jetbrains.annotations.NotNull;
import singeltons.JDAManager;
import singeltons.PlayerVault;
import snippets.Colours;
import snippets.EmojiIds;
import snippets.ErrorMessages;
import utils.EventExtender;

public class Skip implements Command {

    public static void trigger(@NotNull EventExtender event, SlashCommandPayload payload) {
        AudioPlayerExtender player;

        try {
            player = PlayerVault
                    .getInstance()
                    .getPlayer(
                            JDAManager.getInstance().getJDA(applicationId),
                            payload.getGuild().getId()
                    );
        } catch (JDANotFoundException e) {
            event.replyEphemeral(
                    ErrorsKt.standardError(
                            ErrorMessages.PLAYER_NOT_FOUND
                    )
            );
            return;
        } catch (GuildNotFoundException e) {
            event.replyEphemeral(
                    ErrorsKt.standardError(
                            ErrorMessages.NO_PLAYER_IN_GUILD
                    )
            );
            return;
        }

        if (!player.getVoiceChannel().getId().equals(payload.getVoiceChannel().getId())) {
            event.replyEphemeral(
                    ErrorsKt.standardError(
                            ErrorMessages.NO_PLAYER_IN_VC
                    )
            );
            return;
        }

        if (player.getCurrentTrack() == null) {
            event.replyEphemeral(
                    ErrorsKt.standardError(
                            ErrorMessages.NO_PLAYING_TRACK
                    )
            );
            return;
        }

        AudioTrackExtender audioTrack;
        try {
            audioTrack = player.playNext();
        } catch (QueueEmptyException e) {
            event.replyEphemeral(
                    ErrorsKt.emptyError(
                            ErrorMessages.QUEUE_EMPTY
                    )
            );
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
    }
}
