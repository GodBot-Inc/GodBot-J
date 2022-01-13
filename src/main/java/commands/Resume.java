package commands;

import ktSnippets.ErrorsKt;
import ktUtils.*;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import singeltons.JDAManager;
import singeltons.PlayerVault;
import snippets.Colours;
import snippets.EmojiIds;
import snippets.ErrorMessages;
import utils.*;

public class Resume implements Command {

    public static void trigger(SlashCommandEvent scEvent) {
        Guild guild = scEvent.getGuild();
        Member member = scEvent.getMember();
        VoiceChannel voiceChannel;

        EventExtender event = new EventExtender(scEvent);

        try {
            voiceChannel = Checks.slashCommandCheck(
                    applicationId,
                    member,
                    guild
            );
        } catch (VoiceCheckFailedException e) {
            event.replyEphemeral(
                    ErrorsKt.standardError(
                            ErrorMessages.NOT_CONNECTED_TO_VC
                    )
            );
            return;
        } catch (CheckFailedException e) {
            event.replyEphemeral(
                    ErrorsKt.standardError(
                            ErrorMessages.GENERAL_ERROR
                    )
            );
            return;
        }

        AudioPlayerExtender player;

        try {
            player = PlayerVault
                    .getInstance()
                    .getPlayer(
                            JDAManager.getInstance().getJDA(applicationId),
                            guild.getId()
                    );
        } catch (JDANotFound e) {
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

        if (!player.getVoiceChannel().getId().equals(voiceChannel.getId())) {
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
        if (!player.isPaused()) {
            event.replyEphemeral(
                    ErrorsKt.standardError(
                    "Player is not paused"
                    )
            );
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
    }
}
