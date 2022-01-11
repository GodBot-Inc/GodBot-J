package commands;

import io.github.cdimascio.dotenv.Dotenv;
import ktSnippets.ErrorsKt;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.managers.AudioManager;
import org.jetbrains.annotations.NotNull;
import singeltons.AudioManagerVault;
import singeltons.JDAManager;
import snippets.Colours;
import snippets.ErrorMessages;
import utils.CheckFailedException;
import utils.Checks;
import utils.EventExtender;

public class Join implements Command {

    public static void trigger(@NotNull SlashCommandEvent scEvent) {
        Dotenv dotenv = Dotenv.load();
        Guild guild = scEvent.getGuild();
        Member member = scEvent.getMember();
        String applicationId = dotenv.get("APPLICATIONID");

        EventExtender event = new EventExtender(scEvent);

        try {
            Checks.slashCommandCheck(
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

        AudioManager audioManager = AudioManagerVault
                .getInstance()
                .getAudioManager(
                        godbotJDA,
                        guild.getId()
                );

        audioManager.openAudioConnection(
                member.getVoiceState().getChannel()
        );

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
