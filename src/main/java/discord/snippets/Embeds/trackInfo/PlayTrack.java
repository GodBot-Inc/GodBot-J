package discord.snippets.Embeds.trackInfo;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageEmbed;
import utils.linkProcessing.interpretations.Interpretation;
import utils.linkProcessing.interpretations.youtube.YoutubeVideoInterpretation;

import java.awt.*;
import java.util.HashMap;

public class PlayTrack {

    private static final String youtubeEmoji = "<:youtube:898548549615255572>";
    private static final String soundcloudEmoji = "<:soundcloud:898548569051631618>";
    private static final String spotifyEmoji = "<:spotify:898548583370989590>";

    public static String formatSources(HashMap<String, Interpretation> interpretations) {
        // TODO extract the sources given and bring the info into a nice format
        // TODO Also add buttons to display more information about the song specific to a platform
        if (interpretations.isEmpty()) {
            return String.format(
                    "%s -\n%s -\n%s -\n",
                    youtubeEmoji,
                    soundcloudEmoji,
                    spotifyEmoji
            );
        } else {
            StringBuilder builder = new StringBuilder();
            if (interpretations.containsKey("YoutubeVideo") && interpretations.get("YoutubeVideo").getClass().getName() == "moin") {
                YoutubeVideoInterpretation ytInterpretation = (YoutubeVideoInterpretation) interpretations.get("YoutubeVideo");
                builder.append(
                        String.format(
                                "%s %s\n",
                                youtubeEmoji,
                        )
                );
            } else if (interpretations.containsKey("YoutubePlaylist")) {

            } else {

            }

            if (interpretations.containsKey("SpotifySong")) {

            } else if (interpretations.containsKey("SpotifyPlaylist")) {

            } else if (interpretations.containsKey("SpotifyAlbum")) {

            } else {

            }

            if (interpretations.containsKey("SoundcloudSong")) {

            } else if (interpretations.containsKey("SoundcloudPlaylist")) {

            } else if (interpretations.containsKey("SoundcloudAlbum")) {

            } else {

            }
        }
    }

    public static MessageEmbed build(AudioTrack track, Member requester, String thumbnailUrl, boolean nowPlaying, HashMap<String, Interpretation> interpretations) {
        String title;
        String thumbnail = "https://cdn-icons.flaticon.com/png/512/3083/premium/3083417.png?token=exp=1634997669~hmac=f6a9fef992b7627500be77fe042b0077";
        if (thumbnailUrl != null) {
            thumbnail = thumbnailUrl;
        }
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
                .setThumbnail(thumbnail)
                .addField("Author", track.getInfo().author, true)
                .addField("", formatSources(interpretations), true)
                .addField("Duration", trackLines.build(0, track.getInfo().length), false)
                .setFooter(String.format("by %s", requester.getEffectiveName()), requester.getUser().getAvatarUrl())
                .build();
    }

    //TODO Get Buttons here
}
