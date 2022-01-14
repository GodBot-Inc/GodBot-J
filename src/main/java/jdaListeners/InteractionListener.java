package jdaListeners;

import commands.*;
import interactions.ButtonActionsKt;
import ktCommands.GeneralCommandsKt;
import ktCommands.MusicControlCommandsKt;
import ktSnippets.ErrorsKt;
import ktUtils.ButtonException;
import logging.ListenerLogger;
import logging.LoggerContent;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import snippets.ErrorMessages;
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
        switch (event.getName()) {
            case "join" -> Join.trigger(event);
            case "play" -> Play.trigger(event);
            case "pause" -> Pause.trigger(event);
            case "resume" -> Resume.trigger(event);
            case "stop" -> Stop.trigger(event);
            case "skip" -> Skip.trigger(event);
            case "queue" -> Queue.trigger(event);
            case "clear-queue" -> GeneralCommandsKt.clearQueue(new EventExtender(event));
            case "remove" -> MusicControlCommandsKt.remove(new EventExtender(event));
            case "leave" -> GeneralCommandsKt.leave(new EventExtender(event));
            case "loop" -> MusicControlCommandsKt.loop(new EventExtender(event));
            case "skipto" -> MusicControlCommandsKt.skipTo(new EventExtender(event));
            case "volume" -> MusicControlCommandsKt.volume(new EventExtender(event));
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
