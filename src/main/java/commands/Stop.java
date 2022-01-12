package commands;

import io.github.cdimascio.dotenv.Dotenv;
import ktSnippets.ErrorsKt;
import ktUtils.AudioPlayerExtender;
import ktUtils.CheckFailedException;
import ktUtils.GuildNotFoundException;
import ktUtils.JDANotFound;
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

public class Stop implements Command {

    public static void trigger(SlashCommandEvent scEvent) {
        Dotenv dotenv = Dotenv.load();
        Guild guild = scEvent.getGuild();
        Member member = scEvent.getMember();
        String applicationId = dotenv.get("APPLICATIONID");

        EventExtender event = new EventExtender(scEvent);

        VoiceChannel voiceChannel;
        try {
            voiceChannel = Checks.slashCommandCheck(
                    scEvent,
                    applicationId,
                    member,
                    guild
            );
        } catch (CheckFailedException e) {
            event
                    .replyEphemeral(
                            ErrorMessages.GENERAL_ERROR
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

        if (player.getAudioPlayer().getPlayingTrack() == null) {
            event.replyEphemeral(
                    ErrorsKt.standardError(
                            ErrorMessages.NO_PLAYING_TRACK
                    )
            );
        }

        player.clearQueue();
        player.getAudioPlayer().stopTrack();

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
    }
}
