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
        if (interpretation instanceof YoutubePlaylistInterpretation) {
            return String.format(
                    "%s [YouTube](%s)",
                    EmojiIds.youtubeEmoji,
                    interpretation.getUrl()
            );
        } else if (interpretation instanceof SpotifyPlaylistInterpretation) {
            return String.format(
                    "%s [Spotify](%s)",
                    EmojiIds.spotifyEmoji,
                    interpretation.getUrl()
            );
        } else {
            return String.format(
                    "%s -",
                    EmojiIds.coolMusicIcon
            );
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

    private static String formatDuration(Interpretation interpretation) {
        String strDuration = DurationCalc.longToString(interpretation.getDuration());
        int strDurationLength = strDuration.length();
        if (strDurationLength == 3) {
            return String.format("**00:00:00 - %s**", strDuration);
        } else if (strDurationLength == 2) {
            return String.format("**00:00 - %s**", strDuration);
        } else {
            return String.format("**00 - %s**", strDuration);
        }
    }

    @CheckReturnValue
    public static MessageEmbed standard(
            Member requester,
            boolean nowPlaying,
            YoutubePlaylistInterpretation playlistInterpretation
    ) {

        String title = getTitle(playlistInterpretation);

        return new EmbedBuilder()
                .setTitle(title + (nowPlaying ? "Loaded" : "Queued"))
                .setColor(Colours.godbotHeavenYellow)
                .setThumbnail(playlistInterpretation.getThumbnailUrl())
                .addField("Creator", getCreator(playlistInterpretation), true)
                .addField("Sources", formatSource(playlistInterpretation), true)
                .addField("Tracks", String.valueOf(playlistInterpretation.getSize()), false)
                .addField("Total Duration", formatDuration(playlistInterpretation), true)
                .setFooter(
                        String.format(
                                "Added %s", requester.getEffectiveName()
                        ),
                        requester.getUser().getAvatarUrl()
                )
                .build();
    }
}
