package com.godbot.discord.snippets.Embeds.trackInfo;

import com.godbot.discord.snippets.Embeds.Colours;
import com.godbot.discord.snippets.Keys;
import com.godbot.utils.audio.DurationCalc;
import com.godbot.utils.discord.EmojiIds;
import com.godbot.utils.interpretations.Interpretation;
import com.godbot.utils.interpretations.InterpretationExtraction;
import com.godbot.utils.interpretations.spotify.SpotifySongInterpretation;
import com.godbot.utils.interpretations.youtube.YoutubeVideoInterpretation;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageEmbed;

import javax.annotation.CheckReturnValue;
import java.util.HashMap;

public class PlayTrack {

    private static final String defaultThumbnail =
            "https://ppsfbg.am.files.1drv.com/y4pE72hePezfSkxt0hQjVD1oB35c-z2BAKrwscGcHyHGLthmiPiwTo9VgtDyscK3-Dg3Kp4I0z0cdu32TP7gZYoRhobdXoEDuZ4sBCyTblSLK-GN4q21X0_x2M6ybSxJkbRcJbd_k0NCp0Qc7lJHqY9TtCE1GSxS8p5R_M2MdtCDzKM5ZDChWflUiXXss2nzuH734US77ThE-ECcb2bHdVGy8YvgL7H7AEmr7tdZnZ5d4w/music.png";

    public static String getTitle(HashMap<String, Interpretation> interpretations) {
        Interpretation interpretation =
                InterpretationExtraction.getFirstVideoInterpretation(interpretations);

        if (interpretation == null) {
            return "Song";
        }
        return interpretation.getTitle();
    }

    public static String formatSources(HashMap<String, Interpretation> interpretations) {
        StringBuilder builder = new StringBuilder();

        YoutubeVideoInterpretation ytInterpretation = InterpretationExtraction
                .getYTVideoInterpretation(interpretations);
        SpotifySongInterpretation spotifySongInterpretation = InterpretationExtraction
                .getSpotSongInterpretation(interpretations);

        if (ytInterpretation != null && ytInterpretation.getUrl() != null) {
            builder.append(
                    String.format(
                            "%s [YouTube](%s)\n",
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

        if (ytInterpretation != null && ytInterpretation.getMusicUrl() != null) {
            builder.append(
                    String.format(
                            "%s [YouTube Music](%s)\n",
                            EmojiIds.youtubeMusicEmoji,
                            ytInterpretation.getMusicUrl()
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
                            "%s [Spotify](%s)\n",
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
            return " - ";
        }

        if (interpretation.getCreator() == null && interpretation.getCreatorLink() != null) {
            return String.format(
                    "[Creator](%s)",
                    interpretation.getCreatorLink()
            );
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
        long duration = 0;
        if (interpretation != null) {
            duration = interpretation.getDuration();
        }

        String strDuration = DurationCalc.longToString(duration);
        int strDurationLength = DurationCalc.longToString(duration).split(":").length;
        if (strDurationLength == 3) {
            return String.format("**00:00:00 - %s**", strDuration);
        } else if (strDurationLength == 2) {
            return String.format("**00:00 - %s**", strDuration);
        } else {
            return String.format("**00 - %s**", strDuration);
        }
    }

    public static String getDuration(HashMap<String, Interpretation> interpretations) {
        Interpretation firstVideoInterpretation =
                InterpretationExtraction.getFirstVideoInterpretation(interpretations);

        if (firstVideoInterpretation == null || firstVideoInterpretation.getDuration() == 0) {
            return String.format(
                    "%s [00:00 - 00:00]",
                    TrackLines.buildDefault()
            );
        }

        return String.format(
                "%s %s",
                TrackLines.buildDefault(),
                formatDuration(interpretations)
        );
    }

    public static String getPositionInQueue(int position, int queueSize) {
        if (position == 0 && queueSize == 0) {
            return String.format("%s -", EmojiIds.queueEmoji);
        } else if (position == 0) {
            return String.format("%s -/%s", EmojiIds.queueEmoji, queueSize);
        } else if (queueSize == 0) {
            return String.format("%s %s", EmojiIds.queueEmoji, position);
        }
        return String.format("%s %s/%s", EmojiIds.queueEmoji, position, queueSize);
    }

    @CheckReturnValue
    public static MessageEmbed standard(
            Member requester,
            boolean nowPlaying,
            HashMap<String, Interpretation> interpretations,
            int positionInQueue,
            int queueSize
    ) {

        String title = getTitle(interpretations);

        String author = getAuthor(interpretations);

        String duration = getDuration(interpretations);

        String thumbnail = getThumbnail(interpretations);

        return new EmbedBuilder()
                .setTitle(title + " " + (nowPlaying ? "Loaded" : "Queued"))
                .setColor(Colours.godbotHeavenYellow)
                .setThumbnail(thumbnail)
                .addField("Author", author, true)
                .addField("Sources", formatSources(interpretations), true)
                .addField("Position", getPositionInQueue(positionInQueue, queueSize), true)
                .addField("Duration", duration, false)
                .setFooter(
                        String.format(
                                "Added by %s", requester.getEffectiveName()),
                        requester.getUser().getAvatarUrl()
                )
                .build();
    }
}
