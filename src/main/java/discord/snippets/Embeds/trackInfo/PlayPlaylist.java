package discord.snippets.Embeds.trackInfo;

import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageEmbed;
import utils.linkProcessing.interpretations.Interpretation;

import java.awt.*;

public class PlayPlaylist {
    public static String formatSource(Interpretation link) {
        // TODO Add formatting of the source here
        return "";
    }

    public static MessageEmbed embed(AudioPlaylist playlist, String creator, Member requester, String thumbnail, String url, boolean nowPlaying, Interpretation link) {
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
                            url
                        )
                )
                .setColor(Color.ORANGE)
                .setThumbnail(thumbnail)
                .addField("Creator", creator, true)
                .addField(formatSource(link), "", true)
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

    // TODO Get Buttons here
}
