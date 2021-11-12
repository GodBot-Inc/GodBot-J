package utils;

import discord.snippets.Embeds.errors.StandardError;
import discord.snippets.Messages;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import utils.customExceptions.checks.CheckFailedException;
import utils.discord.EventExtender;

public class Checks {

    public static void slashCommandCheck(
            SlashCommandEvent slashCommandEvent,
            String applicationId,
            Member member,
            Guild guild
    ) throws CheckFailedException {
        EventExtender event = new EventExtender(slashCommandEvent);
        if (applicationId == null) {
            event.sendEphermal(
                    StandardError.build("The application was not found")
            );
            throw new CheckFailedException("Application was not found");
        }
        if (guild == null) {
            event.sendEphermal(
                    StandardError.build("Your guild was not found")
            );
            throw new CheckFailedException("Guild is null");
        }
        if (member == null) {
            event.sendEphermal(
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
            event.sendEphermal(
                    StandardError.build(Messages.NOT_CONNECTED_TO_VC)
            );
            throw new CheckFailedException("Member not connected to VC");
        }
    }
}
