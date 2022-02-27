package commands;

import interactions.InteractionScheduler;
import ktSnippets.ErrorsKt;
import ktUtils.*;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import org.bson.Document;
import org.jetbrains.annotations.NotNull;
import singeltons.JDAManager;
import singeltons.PlayerVault;
import snippets.Buttons;
import snippets.Colours;
import snippets.EmojiIds;
import snippets.ErrorMessages;
import utils.DurationCalc;
import utils.EventExtender;
import utils.MongoCommunication;
import utils.QueueWrapper;

import java.util.List;
import java.util.Objects;

public class Queue implements Command {

    public static MessageEmbed getQueueEmbed(
            String description,
            String avatarUrl,
            int currentPage,
            int maxPage
    ) {
        return new EmbedBuilder()
                .setTitle(
                        String.format(
                                "%s Queue",
                                EmojiIds.queueEmoji
                        )
                )
                .setDescription(description)
                .setColor(Colours.godbotYellow)
                .setFooter(
                        String.format(
                                "Page %s/%s",
                                currentPage + 1,
                                maxPage
                        ),
                        avatarUrl
                )
                .build();
    }

    public static void trigger(@NotNull EventExtender event, SlashCommandPayload payload) {
        AudioPlayerExtender audioPlayer;
        try {
            audioPlayer = PlayerVault
                    .getInstance()
                    .getPlayer(
                            JDAManager.getInstance().getJDA(applicationId),
                            payload.getGuild().getId()
                    );
        } catch (JDANotFound e) {
            event.replyEphemeral(
                    ErrorsKt.standardError(
                            ErrorMessages.PLAYER_NOT_FOUND
                    )
            );
            return;
        } catch (GuildNotFoundException e) {
            event.replyEphemeral(
                    ErrorsKt.standardError(
                            ErrorMessages.NO_PLAYER_IN_GUILD
                    )
            );
            return;
        }

        if (!audioPlayer.getVoiceChannel().getId().equals(payload.getVoiceChannel().getId())) {
            event.replyEphemeral(
                    ErrorsKt.standardError(
                            ErrorMessages.NO_PLAYER_IN_VC
                    )
            );
            return;
        }

        List<AudioTrackExtender> queue = audioPlayer.getQueue();
        if (queue.isEmpty()) {
            event.replyEphemeral(
                    ErrorsKt.standardError(
                            ErrorMessages.QUEUE_EMPTY
                    )
            );
            return;
        }

        QueueWrapper.QueueBuilder builder = new QueueWrapper.QueueBuilder();

        builder.setServerId(payload.getGuild().getId());
        builder.setAuthorId(payload.getMember().getId());
        builder.setApplicationId(applicationId);
        builder.setPages((int) Math.ceil((float) audioPlayer.getQueue().size() / 10));

        Document pagesDoc = new Document();
        StringBuilder stringBuilder = new StringBuilder();

        for (int i = 0; i < audioPlayer.getQueue().size(); i++) {
            AudioTrackExtender currentTrack = queue.get(i);
            stringBuilder.append(
                    String.format(
                            "**%s** [%s](%s) [%s] ~ %s\n\n",
                            i+1,
                            currentTrack.getSongInfo().getTitle(),
                            currentTrack.getSongInfo().getUri(),
                            DurationCalc.longToString(currentTrack.getSongInfo().getDuration()),
                            Objects.requireNonNull(currentTrack.getRequester()).getAsMention()
                    )
            );

            if (i % 10 == 9) {
                pagesDoc.append(
                        String.valueOf(i/10),
                        stringBuilder.toString()
                );
                stringBuilder = new StringBuilder();
            } else if (queue.size()-1 == i) {
                pagesDoc.append(
                        String.valueOf(i/10),
                        stringBuilder.toString()
                );
            }
        }

        builder.setPagesDocument(pagesDoc);
        builder.setLastChanged(System.currentTimeMillis());

        if ((int) Math.ceil((float) queue.size() / 10) == 1) {
            event.event.replyEmbeds(
                    getQueueEmbed(
                            (String) pagesDoc.get("0"),
                            payload.getMember().getUser().getAvatarUrl(),
                            0,
                            (int) Math.ceil((float) queue.size() / 10)
                    )
            ).queue();
            return;
        }

        event.event.replyEmbeds(
                    getQueueEmbed(
                            (String) pagesDoc.get("0"),
                            payload.getMember().getUser().getAvatarUrl(),
                            0,
                            (int) Math.ceil((float) queue.size() / 10)
                    )
                )
                .addActionRow(
                        new Buttons.QueueBuilder()
                                .setFirstDisabled(true)
                                .setLeftDisabled(true)
                                .build()
                )
                .queue(interactionHook -> {
                    String messageId = interactionHook.retrieveOriginal().submit().join().getId();
                    builder.setMessageId(messageId);
                    MongoCommunication.getInstance().addQueue(builder.build().toBson());
                    InteractionScheduler interactionScheduler = new InteractionScheduler(
                            event.event.getTextChannel().getId(),
                            messageId,
                            5,
                            new Buttons.QueueBuilder()
                                    .setFirstDisabled(true)
                                    .setLeftDisabled(true)
                                    .setRightDisabled(true)
                                    .setLastDisabled(true)
                                    .buildAsList()
                    );
                    try {
                        interactionScheduler.start().deleteQueue();
                    } catch (ButtonException e) {
                        interactionScheduler.deleteQueue();
                    }
                });
    }
}
