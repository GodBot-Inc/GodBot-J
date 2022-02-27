package commands;

import ktSnippets.ErrorsKt;
import ktUtils.AudioPlayerExtender;
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
        JDA godbotJDA = JDAManager.getInstance().getJDA(applicationId);
        if (godbotJDA == null) {
            event.replyEphemeral(
                    ErrorsKt.standardError(ErrorMessages.GENERAL_ERROR)
            );
            return;
        }

        AudioPlayerExtender audioPlayer;
        try {
            audioPlayer = AudioPlayerManagerWrapper
                    .getInstance()
                    .getPlayer(godbotJDA, payload.getGuild().getId(), payload.getVoiceChannel());
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }

        if (!audioPlayer.getVoiceChannel().equals(payload.getVoiceChannel())) {
            event.replyEphemeral(
                    ErrorsKt.standardError(
                            ErrorMessages.NO_PLAYER_IN_VC
                    )
            );
            return;
        }

        if (audioPlayer.isConnected()) {
            event.replyEphemeral(
                    ErrorsKt.standardError(
                            "I'm already connected to your channel :thinking:"
                    )
            );
            return;
        }

        audioPlayer.openConnection();

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
