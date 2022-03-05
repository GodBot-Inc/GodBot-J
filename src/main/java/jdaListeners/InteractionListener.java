package jdaListeners;

import commands.*;
import interactions.ButtonActionsKt;
import io.github.cdimascio.dotenv.Dotenv;
import ktCommands.GeneralCommandsKt;
import ktCommands.MusicControlCommandsKt;
import ktSnippets.ErrorsKt;
import ktUtils.ButtonException;
import ktUtils.CheckFailedException;
import ktUtils.SlashCommandPayload;
import logging.ListenerLogger;
import logging.LoggerContent;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import snippets.ErrorMessages;
import utils.Checks;
import utils.EventExtender;

import javax.annotation.Nonnull;
import java.util.HashMap;

public class InteractionListener extends ListenerAdapter {

    private final ListenerLogger logger;

    public InteractionListener() {
        this.logger = new ListenerLogger("InteractionListener");
    }

    public HashMap<String, String> getLogArgs(@Nonnull SlashCommandEvent event) {
        User user = event.getUser();
        Guild guild = event.getGuild();
        if (guild == null) {
            return new HashMap<>() {{
                put("GuildId", "null");
                put("AuthorId", user.getId());
                put("GuildName", "null");
                put("AuthorName", user.getName() + user.getDiscriminator());
            }};
        }
        return new HashMap<>() {{
            put("GuildId", guild.getId());
            put("AuthorId", user.getId());
            put("GuildName", guild.getName());
            put("AuthorName", user.getName() + user.getDiscriminator());
        }};
    }

    @Override
    public void onSlashCommand(@Nonnull SlashCommandEvent event) {
        this.logger.info(
                new LoggerContent(
                        "info",
                        "SlashCommandEvent",
                        "",
                        getLogArgs(event)
                )
        );
        // TODO: Put check block here, and always pass the VoiceChannel, if given
        Dotenv dotenv = Dotenv.load();
        String applicationId = dotenv.get("APPLICATIONID");
        Guild guild = event.getGuild();
        Member member = event.getMember();
        VoiceChannel vc;

        try {
            vc = Checks.slashCommandCheck(
                    event,
                    applicationId,
                    member,
                    guild
            );
        } catch (CheckFailedException e) {
            return;
        }

        SlashCommandPayload payload = new SlashCommandPayload(
                vc,
                guild,
                member,
                applicationId
        );

        EventExtender eventExtender = new EventExtender(event);

        switch (event.getName()) {
            case "join" -> Join.trigger(eventExtender, payload);
            case "play" -> Play.trigger(eventExtender, payload);
            case "pause" -> Pause.trigger(eventExtender, payload);
            case "resume" -> Resume.trigger(eventExtender, payload);
            case "stop" -> Stop.trigger(eventExtender, payload);
            case "skip" -> Skip.trigger(eventExtender, payload);
            case "queue" -> Queue.trigger(eventExtender, payload);
            case "clear-queue" -> GeneralCommandsKt.clearQueue(eventExtender, payload);
            case "remove" -> MusicControlCommandsKt.remove(eventExtender, payload);
            case "leave" -> GeneralCommandsKt.leave(eventExtender, payload);
            case "loop" -> MusicControlCommandsKt.loop(eventExtender, payload);
            case "skipto" -> MusicControlCommandsKt.skipTo(eventExtender, payload);
            case "volume" -> MusicControlCommandsKt.volume(eventExtender, payload);
            case "seek" -> MusicControlCommandsKt.seek(eventExtender, payload);
        }
    }

    @Override
    public void onButtonClick(@NotNull ButtonClickEvent event) {
        if (event.getButton() == null) {
            return;
        }
        if (event.getButton().getId() == null) {
            return;
        }
        try {
            switch (event.getButton().getId()) {
                case "queue_first" -> ButtonActionsKt.onQueueFirst(event);
                case "queue_left" -> ButtonActionsKt.onQueueLeft(event);
                case "queue_right" -> ButtonActionsKt.onQueueRight(event);
                case "queue_last" -> ButtonActionsKt.onQueueLast(event);
            }
        } catch (ButtonException e) {
            event.replyEmbeds(
                    ErrorsKt.standardError(
                            ErrorMessages.BUTTON_PRESS_FAILED
                    )
            ).setEphemeral(true).queue();
        }
    }
}
