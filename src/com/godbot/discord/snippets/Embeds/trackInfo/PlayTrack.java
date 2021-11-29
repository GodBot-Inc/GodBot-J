package discord.snippets.Embeds.trackInfo;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import discord.snippets.Keys;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageEmbed;
import utils.EmojiIds;
import utils.audio.DurationCalc;
import utils.interpretations.Interpretation;
import utils.interpretations.soundcloud.SoundCloudInterpretation;

import java.awt.*;
import java.util.HashMap;

public class PlayTrack {

    private static final String defaultThumbnail = "https://cdn-icons.flaticon.com/png/512/3083/premium/3083417.png?token=exp=1634997669~hmac=f6a9fef992b7627500be77fe042b0077";

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
        } else {
            builder.append(
                    String.format(
                            "%s -\n",
                            EmojiIds.youtubeEmoji
                    )
            );
        }

        if (interpretations.containsKey(Keys.SPOTSONG)) {
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

        if (interpretations.containsKey(Keys.SCSONG)) {
            builder.append(
                    String.format(
                            "%s %s",
                            EmojiIds.soundcloudEmoji,
                            interpretations.get(Keys.SCSONG).getUrl()
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
        if (interpretations.containsKey(Keys.SCSONG)) {
            return interpretations.get(Keys.SCSONG).getThumbnailUrl();
        } else {
            return defaultThumbnail;
        }
    }

    private static String formatDuration(HashMap<String, Interpretation> interpretations) {
        if (interpretations.containsKey(Keys.SCSONG)) {
            interpretations.get(Keys.SCSONG).getDuration();
            String strDuration = DurationCalc.longToString(
                    interpretations
                            .get(Keys.SCSONG)
                            .getDuration()
            );
            if (strDuration.split(":").length == 3) {
                return String.format("**00:00:00 - %s**", strDuration);
            } else {
                return String.format("**00:00 - %s**", strDuration);
            }
        } else {
            return "**00:00 - 00:00**";
        }
    }

    private static String getAuthorLink(HashMap<String, Interpretation> interpretations) {
        if (interpretations.containsKey(Keys.SCSONG)) {
            return ((SoundCloudInterpretation) interpretations.get(Keys.SCSONG)).getAuthorUrl();
        } else {
            return "https://soundcloud.com";
        }
    }

    public static MessageEmbed build(
            AudioTrack track,
            Member requester,
            boolean nowPlaying,
            HashMap<String, Interpretation> interpretations
    ) {
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
                        String.format(
                                "[%s](%s)",
                                track.getInfo().author,
                                getAuthorLink(interpretations)
                        ),
                        true)
                .addField("Sources", formatSources(interpretations), true)
                .addField("Duration",
                        String.format(
                                ":radio_button:▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬ `[%s]`",
                                formatDuration(interpretations)
                        ),
                        false)
                .setFooter(
                        String.format(
                                "by %s", requester.getEffectiveName()),
                        requester.getUser().getAvatarUrl()
                )
                .build();
    }
}
