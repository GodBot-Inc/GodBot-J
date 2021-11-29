package discord.snippets.Embeds.trackInfo;

import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageEmbed;
import utils.discord.EmojiIds;
import utils.interpretations.Interpretation;
import utils.interpretations.spotify.SpotifyPlaylistInterpretation;
import utils.interpretations.youtube.YoutubePlaylistInterpretation;

import java.awt.*;

public class PlayPlaylist {
    public static String formatSingelSource(Interpretation interpretation) {
        if (interpretation instanceof YoutubePlaylistInterpretation) {
            return String.format(
                    "%s %s",
                    EmojiIds.youtubeEmoji,
                    interpretation.getUrl()
            );
        } else if (interpretation instanceof SpotifyPlaylistInterpretation) {
            return String.format(
                    "%s %s",
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

    public static MessageEmbed embed(AudioPlaylist playlist, Member requester, boolean nowPlaying, Interpretation link) {
        String title;
        if (nowPlaying) {
            title = "Playing";
        } else {
            title = "Queued";
        }
        return new EmbedBuilder()
                .setTitle(title)
                .setDescription(
                        String.format(
                            "[%s](%s)",
                            playlist.getName(),
                            link.getUrl()
                        )
                )
                .setColor(Color.ORANGE)
                .setThumbnail(link.getThumbnailUrl())
                .addField("Creator", link.getCreator(), true)
                .addField(formatSingelSource(link), "", true)
                .addField("Tracks", String.valueOf(playlist.getTracks().size()), true)
                .setFooter(
                        String.format(
                                "by %s",
                                requester.getNickname()
                        ),
                        requester.getUser().getAvatarUrl()
                )
                .build();
    }
}
