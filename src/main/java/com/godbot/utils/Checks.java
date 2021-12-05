package com.godbot.utils;

import com.godbot.discord.snippets.Embeds.errors.StandardError;
import com.godbot.discord.snippets.Messages;
import com.godbot.utils.customExceptions.checks.CheckFailedException;
import com.godbot.utils.customExceptions.checks.VoiceCheckFailedException;
import com.godbot.utils.discord.EventExtender;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;

public class Checks {

    public static void slashCommandCheck(
            SlashCommandEvent slashCommandEvent,
            String applicationId,
            Member member,
            Guild guild
    ) throws CheckFailedException,
            VoiceCheckFailedException {
        EventExtender event = new EventExtender(slashCommandEvent);
        if (applicationId == null) {
            event.replyEphemeral(
                    StandardError.build("The application was not found")
            );
            throw new CheckFailedException("Application was not found");
        }
        if (guild == null) {
            event.replyEphemeral(
                    StandardError.build("Your guild was not found")
            );
            throw new CheckFailedException("Guild is null");
        }
        if (member == null) {
            event.replyEphemeral(
                    StandardError.build(Messages.GENERAL_ERROR)
            );
            throw new CheckFailedException("Member is null");
        }
        if (
                member.getVoiceState() == null ||
                        member
                                .getVoiceState()
                                .getChannel() == null
        ) {
            event.replyEphemeral(
                    StandardError.build(Messages.NOT_CONNECTED_TO_VC)
            );
            throw new VoiceCheckFailedException("Member not connected to VC");
        }
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
