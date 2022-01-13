package commands;

import io.github.cdimascio.dotenv.Dotenv;
import ktSnippets.ErrorsKt;
import ktUtils.AudioPlayerExtender;
import ktUtils.CheckFailedException;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import org.jetbrains.annotations.NotNull;
import singeltons.AudioPlayerManagerWrapper;
import singeltons.JDAManager;
import snippets.Colours;
import snippets.ErrorMessages;
import utils.Checks;
import utils.EventExtender;

public class Join implements Command {

    public static void trigger(@NotNull SlashCommandEvent scEvent) {
        Dotenv dotenv = Dotenv.load();
        Guild guild = scEvent.getGuild();
        Member member = scEvent.getMember();
        VoiceChannel voiceChannel;
        String applicationId = dotenv.get("APPLICATIONID");

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
                    .getPlayer(godbotJDA, guild.getId(), voiceChannel);
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }

        if (!audioPlayer.getVoiceChannel().equals(voiceChannel)) {
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
                                        "Joined the Channel `%s`",
                                        member.getVoiceState().getChannel().getName()
                                )
                        )
                        .setColor(Colours.godbotYellow)
                        .build()
        );
    }
}
