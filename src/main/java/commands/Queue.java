package commands;

import interactions.InteractionScheduler;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import org.bson.Document;
import org.jetbrains.annotations.NotNull;
import singeltons.JDAManager;
import singeltons.PlayerVault;
import snippets.*;
import utils.*;

import java.util.List;

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

    public static void trigger(@NotNull SlashCommandEvent scEvent) {
        Guild guild = scEvent.getGuild();
        Member member = scEvent.getMember();
        MongoCommunication mongoCommunication = MongoCommunication.getInstance();
        VoiceChannel voiceChannel;

        EventExtender event = new EventExtender(scEvent);

        try {
            voiceChannel = Checks.slashCommandCheck(
                    scEvent,
                    applicationId,
                    member,
                    guild
            );
        } catch (CheckFailedException e) {
            return;
        }

        AudioPlayerExtender audioPlayer;
        try {
            audioPlayer = PlayerVault
                    .getInstance()
                    .getPlayer(
                            JDAManager.getInstance().getJDA(applicationId),
                            guild.getId()
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

        if (!audioPlayer.getVoiceChannel().getId().equals(voiceChannel.getId())) {
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

        builder.setServerId(guild.getId());
        builder.setAuthorId(member.getId());
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
                            currentTrack.getAudioTrack().getInfo().title,
                            currentTrack.getAudioTrack().getInfo().uri,
                            DurationCalc.longToString(currentTrack.getAudioTrack().getInfo().length),
                            currentTrack.getRequester().getAsMention()
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
            scEvent.replyEmbeds(
                    getQueueEmbed(
                            (String) pagesDoc.get("0"),
                            member.getUser().getAvatarUrl(),
                            0,
                            (int) Math.ceil((float) queue.size() / 10)
                    )
            ).queue();
            return;
        }

        scEvent.replyEmbeds(
                    getQueueEmbed(
                            (String) pagesDoc.get("0"),
                            member.getUser().getAvatarUrl(),
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
                    mongoCommunication.addQueue(builder.build().toBson());
                    InteractionScheduler interactionScheduler = new InteractionScheduler(
                            scEvent.getTextChannel().getId(),
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
