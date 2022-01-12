package commands;

import io.github.cdimascio.dotenv.Dotenv;
import ktSnippets.ErrorsKt;
import ktUtils.*;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import org.jetbrains.annotations.NotNull;
import playableInfo.PlayableInfo;
import singeltons.JDAManager;
import singeltons.PlayerVault;
import snippets.Colours;
import snippets.EmojiIds;
import snippets.ErrorMessages;
import utils.*;

public class Skip implements Command {

    public static void trigger(@NotNull SlashCommandEvent scEvent) {
        Dotenv dotenv = Dotenv.load();
        Guild guild = scEvent.getGuild();
        Member member = scEvent.getMember();
        String applicationId = dotenv.get("APPLICATIONID");
        VoiceChannel voiceChannel;

        EventExtender event = new EventExtender(scEvent);

        try {
            voiceChannel = Checks.slashCommandCheck(
                    scEvent,
                    applicationId,
                    member,
                    guild
            );
        } catch (CheckFailedException e) {
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
            return;
        }

        PlayableInfo playableInfo;
        try {
            playableInfo = player.playNext();
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
                                        playableInfo.getTitle(),
                                        playableInfo.getUri()
                                )
                        )
                        .setColor(Colours.godbotYellow)
                        .build()
        );
    }
}
