package com.godbot.discord.commands.musicControl;

import com.godbot.discord.audio.PlayerVault;
import com.godbot.discord.commands.Command;
import com.godbot.discord.snippets.Embeds.errors.StandardError;
import com.godbot.discord.snippets.Messages;
import com.godbot.utils.Checks;
import com.godbot.utils.customExceptions.ChannelNotFoundException;
import com.godbot.utils.customExceptions.GuildNotFoundException;
import com.godbot.utils.customExceptions.checks.CheckFailedException;
import com.godbot.utils.discord.EventExtender;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import io.github.cdimascio.dotenv.Dotenv;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;

public class Resume implements Command {

    public static void trigger(SlashCommandEvent scEvent) {
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
            event.replyEphemeral(
                    StandardError.build(
                            Messages.GENERAL_ERROR
                    )
            );
            return;
        }

        AudioPlayer player;

        try {
            player = PlayerVault
                    .getInstance()
                    .getPlayer(
                            guild.getId(),
                            member
                                    .getVoiceState()
                                    .getChannel()
                                    .getId()
                    );
        } catch (GuildNotFoundException e) {
            event.replyEphemeral(
                    StandardError.build(
                            Messages.NO_PLAYER_IN_GUILD
                    )
            );
            return;
        } catch (ChannelNotFoundException e) {
            event.replyEphemeral(
                    StandardError.build(
                            Messages.NO_PLAYER_IN_VC
                    )
            );
            return;
        }

        if (player.getPlayingTrack() == null) {
            event.replyEphemeral(
                    StandardError.build(
                            Messages.NO_PLAYING_TRACK
                    )
            );
        }
        if (!player.isPaused()) {
            event.replyEphemeral(
                    StandardError.build(
                    "Player is already playing a track"
                    )
            );
        }

        player.setPaused(false);
        scEvent.replyEmbeds(
                new EmbedBuilder()
                        .setDescription(
                                String.format(
                                        ":play_pause: Resumed track [%s - %s](%s)",
                                        player.getPlayingTrack().getInfo().title,
                                        player.getPlayingTrack().getInfo().author,
                                        player.getPlayingTrack().getInfo().uri
                                )
                        )
                        .build()
        )
                .queue();
    }
}
