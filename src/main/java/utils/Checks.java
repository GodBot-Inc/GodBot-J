package utils;

import ktSnippets.ErrorsKt;
import ktUtils.CheckFailedException;
import ktUtils.VoiceCheckFailedException;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import snippets.ErrorMessages;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;

public class Checks {

    public static VoiceChannel slashCommandCheck(
            String applicationId,
            Member member,
            Guild guild
    ) throws CheckFailedException {
        if (applicationId == null) {
            throw new CheckFailedException();
        }
        if (guild == null) {
            throw new CheckFailedException();
        }
        if (member == null) {
            throw new CheckFailedException();
        }
        if (member.getVoiceState() == null || member.getVoiceState().getChannel() == null) {
            throw new VoiceCheckFailedException();
        }
        return member.getVoiceState().getChannel();
    }

    public static VoiceChannel slashCommandCheck(
            SlashCommandEvent slashCommandEvent,
            String applicationId,
            Member member,
            Guild guild
    ) throws CheckFailedException {
        EventExtender event = new EventExtender(slashCommandEvent);
        if (applicationId == null) {
            event.replyEphemeral(
                    ErrorsKt.standardError(ErrorMessages.GENERAL_ERROR)
            );
            throw new CheckFailedException();
        }
        if (guild == null) {
            event.replyEphemeral(
                    ErrorsKt.standardError(ErrorMessages.GENERAL_ERROR)
            );
            throw new CheckFailedException();
        }
        if (member == null) {
            event.replyEphemeral(
                    ErrorsKt.standardError(ErrorMessages.GENERAL_ERROR)
            );
            throw new CheckFailedException();
        }
        if (
                member.getVoiceState() == null ||
                        member
                                .getVoiceState()
                                .getChannel() == null
        ) {
            event.replyEphemeral(
                    ErrorsKt.standardError(ErrorMessages.NOT_CONNECTED_TO_VC)
            );
            throw new VoiceCheckFailedException();
        }

        return member.getVoiceState().getChannel();
    }

    public static VoiceChannel slashCommandCheck(
            EventExtender event,
            String applicationId,
            Member member,
            Guild guild
    ) throws CheckFailedException {
        if (applicationId == null) {
            event.replyEphemeral(
                    ErrorsKt.standardError(ErrorMessages.GENERAL_ERROR)
            );
            throw new CheckFailedException();
        }
        if (guild == null) {
            event.replyEphemeral(
                    ErrorsKt.standardError(ErrorMessages.GENERAL_ERROR)
            );
            throw new CheckFailedException();
        }
        if (member == null) {
            event.replyEphemeral(
                    ErrorsKt.standardError(ErrorMessages.GENERAL_ERROR)
            );
            throw new CheckFailedException();
        }
        if (
                member.getVoiceState() == null ||
                        member
                                .getVoiceState()
                                .getChannel() == null
        ) {
            event.replyEphemeral(
                    ErrorsKt.standardError(ErrorMessages.NOT_CONNECTED_TO_VC)
            );
            throw new VoiceCheckFailedException();
        }

        return member.getVoiceState().getChannel();
    }

    public static boolean linkIsValid(String url) {
        try {
            new URL(url).toURI();
            return false;
        } catch (MalformedURLException | URISyntaxException e) {
            return true;
        }
    }
}
