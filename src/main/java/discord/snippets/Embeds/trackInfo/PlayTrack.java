package discord.snippets.Embeds.trackInfo;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageEmbed;
import utils.linkProcessing.interpretations.Interpretation;

import java.awt.*;
import java.util.List;

public class PlayTrack {
    public static String formatSources(List<Interpretation> linkList) {
        // TODO extract the sources given and bring the info into a nice format
        // TODO Also add buttons to display more information about the song specific to a platform
        return "";
    }

    public static MessageEmbed build(AudioTrack track, Member requester, String thumbnail, boolean nowPlaying, List<Interpretation> linkList) {
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
                .setThumbnail(thumbnail)
                .addField("Author", track.getInfo().author, true)
                .addField(formatSources(linkList), "", true)
                .addField("Duration", trackLines.build(0, track.getInfo().length), false)
                .setFooter(String.format("by %s", requester.getNickname()), requester.getUser().getAvatarUrl())
                .build();
    }

    //TODO Get Buttons here
}
