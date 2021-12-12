package com.godbot.discord.snippets.Embeds.trackInfo;

import com.godbot.discord.snippets.Embeds.Colours;
import com.godbot.utils.audio.DurationCalc;
import com.godbot.utils.discord.EmojiIds;
import com.godbot.utils.interpretations.Interpretation;
import com.godbot.utils.interpretations.spotify.SpotifyPlaylistInterpretation;
import com.godbot.utils.interpretations.youtube.YoutubePlaylistInterpretation;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageEmbed;

import javax.annotation.CheckReturnValue;

public class PlayPlaylist {

    private static String getTitle(YoutubePlaylistInterpretation interpretation) {
        if (interpretation.getTitle() == null) {
            return "Playlist";
        }
        return interpretation.getTitle();
    }

    public static String formatSource(Interpretation interpretation) {
        StringBuilder stringBuilder = new StringBuilder();

        if (interpretation instanceof YoutubePlaylistInterpretation) {
            stringBuilder.append(
                    String.format(
                            "%s [YouTube](%s)\n" +
                                    "%s [YouTube Music](%s)\n",
                            EmojiIds.youtubeEmoji,
                            interpretation.getUrl(),
                            EmojiIds.youtubeMusicEmoji,
                            ((YoutubePlaylistInterpretation) interpretation).getMusicUri()
                    )
            );
        } else {
            stringBuilder.append(
                    String.format(
                            "%s YouTube -\n" +
                                    "%s YouTube Music\n",
                            EmojiIds.youtubeEmoji,
                            EmojiIds.youtubeMusicEmoji
                    )
            );
        }
        if (interpretation instanceof SpotifyPlaylistInterpretation) {
            stringBuilder.append(
                    String.format(
                            "%s [Spotify](%s)",
                            EmojiIds.spotifyEmoji,
                            interpretation.getUrl()
                    )
            );
        } else {
            stringBuilder.append(
                    String.format(
                            "%s Spotify",
                            EmojiIds.spotifyEmoji
                    )
            );
        }
        return stringBuilder.toString();
    }

    private static String formatDuration(Interpretation interpretation) {
        if (interpretation == null) {
            return "**00:00 - 00:00**";
        }

        long duration = interpretation.getDuration();

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

    private static String getCreator(Interpretation interpretation) {
        if (interpretation.getCreator() == null && interpretation.getCreatorLink() == null) {
            return " - ";
        } else if (interpretation.getCreator() == null && interpretation.getCreatorLink() != null) {
            return String.format(
                    "[Author](%s)",
                    interpretation.getCreatorLink()
            );
        } else if (interpretation.getCreatorLink() == null) {
            return interpretation.getCreator();
        }

        return String.format(
                "[%s](%s)",
                interpretation.getCreator(),
                interpretation.getCreatorLink()
        );
    }

    public static String getDuration(Interpretation interpretation) {
        if (interpretation.getDuration() == 0) {
            return String.format(
                    "%s -",
                    TrackLines.buildDefault()
            );
        }

        return String.format(
                "%s %s",
                TrackLines.buildDefault(),
                formatDuration(interpretation)
        );
    }

    @CheckReturnValue
    public static MessageEmbed standard(
            Member requester,
            boolean nowPlaying,
            YoutubePlaylistInterpretation playlistInterpretation
    ) {

        String title = getTitle(playlistInterpretation);

        return new EmbedBuilder()
                .setTitle(title + " " + (nowPlaying ? "Loaded" : "Queued"))
                .setColor(Colours.godbotHeavenYellow)
                .setThumbnail(playlistInterpretation.getThumbnailUrl())
                // TODO Remove Creator and add Position in Queue instead (after Tracks)
                .addField("Creator", getCreator(playlistInterpretation), true)
                .addField("Sources", formatSource(playlistInterpretation), true)
                .addField(
                        "Tracks",
                        String.format(
                            "%s %s",
                            EmojiIds.trackEmoji,
                            playlistInterpretation.getSize()
                        ),
                        true
                )
                .addField("Total Duration", getDuration(playlistInterpretation), false)
                .setFooter(
                        String.format(
                                "Added %s", requester.getEffectiveName()
                        ),
                        requester.getUser().getAvatarUrl()
                )
                .build();
    }
}
