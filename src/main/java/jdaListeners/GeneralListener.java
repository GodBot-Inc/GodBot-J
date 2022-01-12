package jdaListeners;

import io.github.cdimascio.dotenv.Dotenv;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceMoveEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import singeltons.JDAManager;
import singeltons.PlayerVault;
import ktUtils.AudioPlayerExtender;
import ktUtils.GuildNotFoundException;
import ktUtils.JDANotFound;

public class GeneralListener extends ListenerAdapter {

    private final Dotenv dotenv = Dotenv.load();
    private final String applicationId = dotenv.get("APPLICATIONID");

    @Override
    public void onGuildVoiceMove(@NotNull GuildVoiceMoveEvent event) {
        if (event.getMember().getId().equals(applicationId)) {
            try {
                AudioPlayerExtender audioPlayerExtender;
                audioPlayerExtender = PlayerVault.getInstance().getPlayer(
                        JDAManager.getInstance().getJDA(applicationId),
                        event.getGuild().getId()
                );
                audioPlayerExtender.changeChannel(event.getChannelJoined());
            } catch (JDANotFound | GuildNotFoundException ignore) {}
        }
    }
}
