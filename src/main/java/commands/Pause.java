package commands;

import ktSnippets.ErrorsKt;
import ktUtils.AudioPlayerExtender;
import ktUtils.GuildNotFoundException;
import ktUtils.JDANotFoundException;
import ktUtils.SlashCommandPayload;
import net.dv8tion.jda.api.EmbedBuilder;
import singeltons.JDAManager;
import singeltons.PlayerVault;
import snippets.Colours;
import snippets.EmojiIds;
import snippets.ErrorMessages;
import utils.EventExtender;

public class Pause implements Command {

    public static void trigger(EventExtender event, SlashCommandPayload payload) {
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
        if (player.isPaused()) {
            event.replyEphemeral(
                    ErrorsKt.standardError(
                            "Player is already paused"
                    )
            );
            return;
        }

        player.setPaused(true);
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
