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
            return " - ";
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

    public static String getDescription(HashMap<String, Interpretation> interpretations) {
        System.out.println("GetDescription triggered");
        Interpretation videoInterpretation =
                InterpretationExtraction.getFirstVideoInterpretation(interpretations);

        System.out.println("after video gotten");

        StringBuilder stringBuilder = new StringBuilder();

        System.out.println("got stringbuilder");

        if (videoInterpretation == null) {
            System.out.println("video interpretation null");
            return "[AudioTrack](https://music.youtube.com)";
        }

        System.out.println("after video interpretation");

        if (videoInterpretation.getTitle() != null) {
            System.out.println("title not null");
            stringBuilder.append(String.format("[%s]", videoInterpretation.getTitle()));
        } else {
            System.out.println("title null");
            stringBuilder.append("[AudioTrack]");
        }

        if (videoInterpretation.getUrl() != null) {
            System.out.println("url not null");
            stringBuilder.append(String.format("(%s)", videoInterpretation.getUrl()));
        } else {
            System.out.println("url null");
            stringBuilder.append("(https://music.youtube.com)");
        }

        System.out.println("returning");

        return stringBuilder.toString();
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
                TrackLines.build(0, firstVideoInterpretation.getDuration()),
                formatDuration(interpretations)
        );
    }

    @CheckReturnValue
    public static MessageEmbed build(
            Member requester,
            boolean nowPlaying,
            HashMap<String, Interpretation> interpretations
    ) {
        System.out.println("triggered");
        System.out.println(interpretations);

        String description = getDescription(interpretations);
        System.out.println("set description");

        String author = getAuthor(interpretations);
        System.out.println("set author");

        String duration = getDuration(interpretations);
        System.out.println("got duration");

        String thumbnail = getThumbnail(interpretations);
        System.out.println("got thumbnail");

        return new EmbedBuilder()
                .setTitle("Song " + (nowPlaying ? "Loaded" : "Queued"))
                .setDescription(description)
                .setColor(Colours.godbotYellow)
                .setThumbnail(thumbnail)
                .addField("Author", author, true)
                .addField("Sources", formatSources(interpretations), true)
                .addField("Duration", duration, false)
                .setFooter(
                        String.format(
                                "Added %s", requester.getEffectiveName()),
                        requester.getUser().getAvatarUrl()
                )
                .build();
    }
}
