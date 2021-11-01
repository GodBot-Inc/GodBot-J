package discord.commands.music;

import discord.audio.*;
import discord.audio.lavaplayer.AudioPlayerSendHandler;
import discord.audio.lavaplayer.AudioResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import discord.snippets.Embeds.errors.NotFoundError;
import discord.snippets.Embeds.errors.StandardError;
import discord.snippets.Embeds.trackInfo.PlayTrack;
import io.github.cdimascio.dotenv.Dotenv;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.audio.AudioSendHandler;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.managers.AudioManager;
import discord.JDAManager;
import okhttp3.internal.platform.Platform;
import utils.customExceptions.ChannelNotFound;
import utils.customExceptions.GuildNotFound;
import utils.customExceptions.LinkInterpretation.InvalidPlatform;
import utils.customExceptions.LinkInterpretation.InvalidURL;
import utils.customExceptions.LinkInterpretation.PlatformNotFound;
import utils.customExceptions.audio.ApplicationNotFound;
import utils.linkProcessing.LinkInterpreter;
import utils.linkProcessing.interpretations.Interpretation;

import java.security.KeyException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

public class Play {
    private static AudioManager getManager(String applicationId, Guild guild) throws KeyException {
        AudioManagerVault audioVault = AudioManagerVault.getInstance();
        try {
            return audioVault.getAudioManager(applicationId, guild.getId());
        } catch (GuildNotFound guildNotFound) {
            audioVault.registerGuild(applicationId, guild);
            return getManager(applicationId, guild);
        } catch (ApplicationNotFound applicationNotFound) {
            JDAManager jdaManager = JDAManager.getInstance();
            JDA godbotJDA = jdaManager.getJDA("godbot");
            audioVault.registerJDA(applicationId, godbotJDA.getGuilds());
            return getManager(applicationId, guild);
        }
    }

    public static void trigger(SlashCommandEvent event, String url) {
        Dotenv dotenv = Dotenv.load();
        Guild guild = event.getGuild();
        Member member = event.getMember();
        String applicationId = dotenv.get("APPLICATIONID");
        if (applicationId == null) {
            event
                    .replyEmbeds(StandardError.build("Could not get the ApplicationId"))
                    .setEphemeral(true)
                    .queue();
            return;
        }
        if (guild == null) {
            event
                    .replyEmbeds(StandardError.build("Could not get the Guild I'm in"))
                    .setEphemeral(true)
                    .queue();
            return;
        }
        if (member == null) {
            event
                    .replyEmbeds(StandardError.build("Could not get the author of the message"))
                    .setEphemeral(true)
                    .queue();
            return;
        }
        if (member.getVoiceState() == null) {
            event
                    .replyEmbeds(StandardError.build("You are not connected to a Voicechannel"))
                    .setEphemeral(true)
                    .queue();
            return;
        }
        if (member.getVoiceState().getChannel() == null) {
            event
                    .replyEmbeds(StandardError.build("You are not connected to a Voicechannel"))
                    .setEphemeral(true)
                    .queue();
            return;
        }
        AudioManager manager;
        try {
            manager = getManager(applicationId, guild);
        } catch (KeyException e) {
            event
                    .replyEmbeds(StandardError.build("Could not get an AudioManager"))
                    .setEphemeral(true)
                    .queue();
            return;
        }
        final AudioPlayer player;
        AudioPlayer player1;
        try {
            player1 = PlayerVault
                    .getInstance()
                    .getPlayer(guild.getId(), member.getVoiceState().getChannel().getId());
        } catch (GuildNotFound | ChannelNotFound e) {
            player1 = PlayerManager
                    .getInstance()
                    .createPlayer(guild.getId(), member.getVoiceState().getChannel().getId());
        }
        player = player1;
        AudioSendHandler handler = manager.getSendingHandler();
        if (handler == null) {
            manager.setSendingHandler(new AudioPlayerSendHandler(player));
        }
        // TODO: Utilize Apis to search for compatibility
        // TODO Find a way to distribute YoutubeInterpretations
        /*
        1. Check if the Url is valid
        2. defer reply
        3. Play the song
         */
        /*
        TODO Get soundcloud searcher first then play and then gather info
        Currently LInkInterpreter.interpret gathers information about the song on all platforms
        which is time consuming and leads to a lot of latency when using the play command.
        What we can do is determine the platform, check the URL etc. and then only get the information we need.
        We get the title and the author of the song, then check if it exists on soundcloud. We get the soundcloud
        Url and pass lavaplayer the song Id so it can get it.
        After the song started playing, we interpret the
         */
        /*
        NOTE: First concentrate on playing videos playlists can be handled later (if the playlist exists
        on soundcloud we can directly load it from soundcloud without lodaing every single song)
         */
        if (!LinkInterpreter.isValid(url)) {
            event
                    .replyEmbeds(StandardError.build("The Url " + url + " is invalid"))
                    .setEphemeral(true)
                    .queue();
            return;
        }
        try {
            String platform = LinkInterpreter.getPlatform(url);
        } catch(PlatformNotFound e) {
            event
                    .replyEmbeds(
                            StandardError.build(
                                    "We could not find the corresponding platform to your url"
                            )
                    )
                    .setEphemeral(true)
                    .queue();
            return;
        }

        // TODO: Move connecting to Voicechannel into AudioResultHandler
        manager.openAudioConnection(member.getVoiceState().getChannel());

        // TODO find a way to get information from AudioResultHandler after loading the title
        /*
        Possible Solution:
        We create an instance of a new AudioResultHandler here and create an instance-variable that
        contains information after it got a result. We can get these Information after Items
        were loaded :5head:
         */
        // TODO Determine the platform and get the soundcloud ID
        event.deferReply().queue();
        AudioResultHandler audioResultHandler = new AudioResultHandler(player, event, url);
        PlayerManager
                .getInstance()
                .getManager()
                .loadItem(
                        String.format("%s", url),
                        audioResultHandler
                );
        if (Objects.equals(audioResultHandler.actionType, "trackLoaded")) {

        }
//        HashMap<String, Interpretation> interpretationHashMap = new HashMap<>();
//        try {
//            interpretationHashMap = LinkInterpreter.interpret(url);
//        } catch(InvalidURL e) {
//            event
//                    .replyEmbeds(
//                            StandardError.build("The passed Url is not valid")
//                    )
//                    .queue();
//        } catch(PlatformNotFound e) {
//            event
//                    .replyEmbeds(
//                            StandardError.build(
//                                    "Could not determine the platform that the link was taken from"
//                            )
//                    )
//                    .queue();
//        } catch(InvalidPlatform e) {
//            event
//                    .replyEmbeds(
//                            StandardError.build(
//                                    "The platform passed is invalid"
//                            )
//                    )
//                    .queue();
//        }
    }
}
