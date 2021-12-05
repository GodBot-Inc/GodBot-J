package com.godbot.discord.snippets.Embeds.trackInfo;

import com.godbot.discord.snippets.Keys;
import com.godbot.utils.audio.DurationCalc;
import com.godbot.utils.customExceptions.LinkInterpretation.InterpretationsEmpty;
import com.godbot.utils.discord.EmojiIds;
import com.godbot.utils.interpretations.Interpretation;
import com.godbot.utils.interpretations.InterpretationExtraction;
import com.godbot.utils.interpretations.spotify.SpotifySongInterpretation;
import com.godbot.utils.interpretations.youtube.YoutubeVideoInterpretation;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageEmbed;

import java.awt.*;
import java.util.HashMap;

public class PlayTrack {

    private static final String defaultThumbnail = "https://1drv.ms/u/s!AlVdLKL47GJiyDZp4Xj_HoD5eWV4?e=mPMGL6";

    public static String formatSources(HashMap<String, Interpretation> interpretations) {
        StringBuilder builder = new StringBuilder();
        YoutubeVideoInterpretation ytInterpretation = InterpretationExtraction
                .getYTVideoInterpretation(interpretations);
        SpotifySongInterpretation spotifySongInterpretation = InterpretationExtraction
                .getSpotSongInterpretation(interpretations);
        if (ytInterpretation != null) {
            builder.append(
                    String.format(
                            "%s %s\n",
                            EmojiIds.youtubeEmoji,
                            ytInterpretation.getUrl()
                    )
            );
        } else {
            builder.append(
                    String.format(
                            "%s -\n",
                            EmojiIds.youtubeEmoji
                    )
            );
        }

        if (spotifySongInterpretation != null) {
            builder.append(
                    String.format(
                            "%s %s\n",
                            EmojiIds.spotifyEmoji,
                            interpretations.get(Keys.SPOTSONG).getUrl()
                    )
            );
        } else {
            builder.append(
                    String.format(
                            "%s -\n",
                            EmojiIds.spotifyEmoji
                    )
            );
        }

        return builder.toString();
    }

    private static String getAuthor(HashMap<String, Interpretation> interpretations) {
        Interpretation interpretation = InterpretationExtraction.getFirstVideoInterpretation(interpretations);
        if (interpretation == null) {
            return "No Author Found";
        }
        return String.format(
                "[%s](%s)",
                interpretation.getCreator(),
                interpretation.getCreatorLink()
        );
    }

    private static String getThumbnail(HashMap<String, Interpretation> interpretations) {
        Interpretation interpretation = InterpretationExtraction.getFirstVideoInterpretation(interpretations);
        if (interpretation == null) {
            return defaultThumbnail;
        }
        return interpretation.getThumbnailUrl();
    }

    private static String formatDuration(HashMap<String, Interpretation> interpretations) {
        Interpretation interpretation = InterpretationExtraction.getFirstVideoInterpretation(interpretations);
        if (interpretation == null) {
            return "**00:00 - 00:00**";
        }
        String strDuration = DurationCalc.longToString(interpretation.getDuration());
        int strDurationLength = DurationCalc.longToString(interpretation.getDuration()).length();
        if (strDurationLength == 3) {
            return String.format("**00:00:00 - %s**", strDuration);
        } else if (strDurationLength == 2) {
            return String.format("**00:00 - %s**", strDuration);
        } else {
            return String.format("**00 - %s**", strDuration);
        }
    }

    public static MessageEmbed build(
            AudioTrack track,
            Member requester,
            boolean nowPlaying,
            HashMap<String, Interpretation> interpretations
    ) throws InterpretationsEmpty {
        return new EmbedBuilder()
                .setTitle(nowPlaying ? "Playing" : "Queued")
                .setDescription(
                        String.format(
                                "[%s](%s)",
                                track.getInfo().title,
                                track.getInfo().uri
                        )
                )
                .setColor(Color.ORANGE)
                .setThumbnail(getThumbnail(interpretations))
                .addField(
                        "Author",
                        getAuthor(interpretations),
                        true
                )
                .addField("Sources", formatSources(interpretations), true)
                .addField("Duration",
                        String.format(
                                ":radio_button:▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬ `[%s]`",
                                formatDuration(interpretations)
                        ),
                        false)
                .setFooter(
                        String.format(
                                "Added %s", requester.getEffectiveName()),
                        requester.getUser().getAvatarUrl()
                )
                .build();
    }
}
