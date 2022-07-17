package utils;

import io.github.cdimascio.dotenv.Dotenv;
import ktSnippets.ErrorsKt;
import ktUtils.CheckFailedException;
import ktUtils.ENVCheckFailedException;
import ktUtils.EventExtender;
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

    public static boolean linkIsValid(String url) {
        try {
            new URL(url).toURI();
            return false;
        } catch (MalformedURLException | URISyntaxException e) {
            return true;
        }
    }

    public static void checkENV() throws ENVCheckFailedException {
        Dotenv dotenv = Dotenv.load();

        // Main Bot Check
        if (dotenv.get("APPLICATIONID") == null)
            throw new ENVCheckFailedException("APPLICATIONID is missing");
        if (dotenv.get("TOKEN") == null)
            throw new ENVCheckFailedException("TOKEN is missing");

        // Angel Check
        if (dotenv.get("IsrafilAPPLICATIONID") == null)
            throw new ENVCheckFailedException("IsrafilAPPLICATIONID is missing");

        if (dotenv.get("IsrafilTOKEN") == null)
            throw new ENVCheckFailedException("IsrafilTOKEN is missing");

        // DB Check
        if (dotenv.get("DBUSERNAME") == null)
            throw new ENVCheckFailedException("DBUSERNAME is missing (MongoDB)");
        if (dotenv.get("DBPASSWORD") == null)
            throw new ENVCheckFailedException("DBPASSWORD is missing (MongoDB)");

        // YT API Check
        if (dotenv.get("YT_API_KEY") == null)
            throw new ENVCheckFailedException("YT_API_KEY is missing");

        // Spotify API
        if (dotenv.get("SPOT_CLIENT_ID") == null)
            throw new ENVCheckFailedException("SPOT_CLIENT_ID is missing");
        if (dotenv.get("SPOT_CLIENT_SECRET") == null)
            throw new ENVCheckFailedException("SPOT_CLIENT_SECRET");
    }
}
