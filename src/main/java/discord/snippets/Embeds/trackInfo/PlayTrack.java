package discord.snippets.Embeds.trackInfo;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageEmbed;
import utils.EmojiIds;
import utils.linkProcessing.interpretations.Interpretation;

import java.awt.*;
import java.util.HashMap;

public class PlayTrack {
    public static String formatSources(HashMap<String, Interpretation> interpretations) {
        StringBuilder builder = new StringBuilder();
        if (interpretations.containsKey("YoutubeVideo")) {
            builder.append(
                    String.format(
                            "%s %s\n",
                            EmojiIds.youtubeEmoji,
                            interpretations.get("YoutubeVideo").getUrl()
                    )
            );
        } else if (interpretations.containsKey("YoutubePlaylist")) {
            builder.append(
                    String.format(
                            "%s %s\n",
                            EmojiIds.youtubeEmoji,
                            interpretations.get("YoutubePlaylist").getUrl()
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

        if (interpretations.containsKey("SpotifySong")) {
            builder.append(
                    String.format(
                            "%s %s\n",
                            EmojiIds.spotifyEmoji,
                            interpretations.get("SpotifySong").getUrl()
                    )
            );
        } else if (interpretations.containsKey("SpotifyPlaylist")) {
            builder.append(
                    String.format(
                            "%s %s\n",
                            EmojiIds.spotifyEmoji,
                            interpretations.get("SpotifyPlaylist").getUrl()
                    )
            );
        } else if (interpretations.containsKey("SpotifyAlbum")) {
            builder.append(
                    String.format(
                            "%s %s\n",
                            EmojiIds.spotifyEmoji,
                            interpretations.get("SpotifyAlbum").getUrl()
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

        if (interpretations.containsKey("SoundcloudSong")) {
            builder.append(
                    String.format(
                            "%s %s",
                            EmojiIds.soundcloudEmoji,
                            interpretations.get("SoundcloudSong").getUrl()
                    )
            );
        } else if (interpretations.containsKey("SoundcloudPlaylist")) {
            builder.append(
                    String.format(
                            "%s %s",
                            EmojiIds.soundcloudEmoji,
                            interpretations.get("SoundcloudPlaylist").getUrl()
                    )
            );
        } else if (interpretations.containsKey("SoundcloudAlbum")) {
            builder.append(
                    String.format(
                            "%s %s",
                            EmojiIds.soundcloudEmoji,
                            interpretations.get("SoundcloudAlbum")
                    )
            );
        } else {
            builder.append(
                    String.format(
                            "%s -",
                            EmojiIds.soundcloudEmoji
                    )
            );
        }
        return builder.toString();
    }

    private static String getThumbnail(HashMap<String, Interpretation> interpretations) {
        String defaultThumbnail = "https://cdn-icons.flaticon.com/png/512/3083/premium/3083417.png?token=exp=1634997669~hmac=f6a9fef992b7627500be77fe042b0077";
        for (Interpretation interpretation : interpretations.values()) {
            if (interpretation.getThumbnailUrl() != null) {
                return interpretation.getThumbnailUrl();
            }
        }
        return defaultThumbnail;
    }

    public static MessageEmbed build(AudioTrack track, Member requester, boolean nowPlaying, HashMap<String, Interpretation> interpretations) {
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
                                track.getInfo().title,
                                track.getInfo().uri
                        )
                )
                .setColor(Color.ORANGE)
                .setThumbnail(getThumbnail(interpretations))
                .addField("Author", track.getInfo().author, true)
                .addField("", formatSources(interpretations), true)
                .addField("Duration", trackLines.build(0, track.getInfo().length), false)
                .setFooter(String.format("by %s", requester.getEffectiveName()), requester.getUser().getAvatarUrl())
                .build();
    }
}
