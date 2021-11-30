package discord.snippets.Embeds.trackInfo;

import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageEmbed;
import utils.audio.DurationCalc;
import utils.discord.EmojiIds;
import utils.interpretations.Interpretation;
import utils.interpretations.InterpretationExtraction;
import utils.interpretations.spotify.SpotifyPlaylistInterpretation;
import utils.interpretations.youtube.YoutubePlaylistInterpretation;

import java.awt.*;
import java.util.HashMap;

public class PlayPlaylist {
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
        if (interpretation.getCreatorLink() == null) {
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

    public static MessageEmbed build(
            AudioPlaylist playlist,
            Member requester,
            boolean nowPlaying,
            Interpretation playlistInterpretation
    ) {
        return new EmbedBuilder()
                .setTitle(nowPlaying ? "Playing" : "Queued")
                .setDescription(
                        String.format(
                            "[%s](%s)",
                            playlist.getName(),
                            playlistInterpretation.getUrl()
                        )
                )
                .setColor(Color.ORANGE)
                .setThumbnail(playlistInterpretation.getThumbnailUrl())
                .addField("Creator", getCreator(playlistInterpretation), true)
                .addField("Sources", formatSource(playlistInterpretation), true)
                .addField("Tracks", String.valueOf(playlist.getTracks().size()), false)
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
