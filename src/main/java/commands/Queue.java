package commands;

import constants.EmojisKt;
import interactions.InteractionScheduler;
import ktLogging.UtilsKt;
import ktLogging.custom.GodBotChildLogger;
import ktLogging.custom.GodBotLogger;
import ktUtils.ButtonException;
import ktUtils.ErrorHandlerKt;
import ktUtils.FormatterKt;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import objects.AudioPlayerExtender;
import objects.AudioTrackExtender;
import objects.EventFacade;
import objects.SlashCommandPayload;
import org.bson.Document;
import org.jetbrains.annotations.NotNull;
import singeltons.JDAManager;
import singeltons.PlayerVault;
import snippets.Buttons;
import snippets.Colours;
import snippets.ErrorMessages;
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
                                EmojisKt.getQueueEmoji().getAsMention()
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

    public static void trigger(@NotNull EventFacade event, SlashCommandPayload payload) {
        GodBotChildLogger logger = new GodBotLogger().command(
                "Queue",
                UtilsKt.formatPayload(payload)
        );

        AudioPlayerExtender audioPlayer = PlayerVault
                .getInstance()
                .getPlayer(
                        JDAManager.getInstance().getJDA(applicationId),
                        payload.getGuild().getId()
                );
        if (audioPlayer == null) {
            ErrorHandlerKt.handleDefaultErrorResponse(event, payload, ErrorMessages.NO_PLAYER_FOUND, logger);
            return;
        }

        logger.info("Got AudioPlayer");

        if (!audioPlayer.getVoiceChannel().getId().equals(payload.getVoiceChannel().getId())) {
            ErrorHandlerKt.handleDefaultErrorResponse(event, payload, ErrorMessages.NO_PLAYER_IN_VC, logger);
            return;
        }

        List<AudioTrackExtender> queue = audioPlayer.getQueue();
        if (queue.isEmpty()) {
            ErrorHandlerKt.handleDefaultErrorResponse(event, payload, ErrorMessages.QUEUE_EMPTY, logger);
            return;
        }

        QueueWrapper.QueueBuilder builder = new QueueWrapper.QueueBuilder();

        builder.setServerId(payload.getGuild().getId());
        builder.setAuthorId(payload.getMember().getId());
        builder.setApplicationId(applicationId);
        builder.setPages((int) Math.ceil((float) audioPlayer.getQueue().size() / 10));

        Document pagesDoc = new Document();
        StringBuilder stringBuilder = new StringBuilder();
        logger.info("Set preData for QueueBuilder");

        for (int i = 0; i < audioPlayer.getQueue().size(); i++) {
            AudioTrackExtender currentTrack = queue.get(i);
            stringBuilder.append(
                    String.format(
                            "**%s** [%s](%s) [%s] ~ %s\n\n",
                            i+1,
                            currentTrack.getSongInfo().getTitle(),
                            currentTrack.getSongInfo().getUri(),
                            FormatterKt.millisToString(currentTrack.getSongInfo().getDuration()),
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
        logger.info("Built Queue Message with all pages");

        if ((int) Math.ceil((float) queue.size() / 10) == 1) {
            event.reply(
                    getQueueEmbed(
                            (String) pagesDoc.get("0"),
                            payload.getMember().getUser().getAvatarUrl(),
                            0,
                            (int) Math.ceil((float) queue.size() / 10)
                    )
            );
            return;
        }

        event.getEvent().replyEmbeds(
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
                            event.getTextChannel().getId(),
                            messageId,
                            10,
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
        logger.info("Queue Message Sent");
    }
}
