package singeltons;

import utils.AudioPlayerExtender;
import utils.GuildNotFoundException;
import utils.JDANotFound;
import net.dv8tion.jda.api.JDA;

import java.util.HashMap;


public class PlayerVault {

    private static final PlayerVault vaultObj = new PlayerVault();
    //                    Bot         GuildId
    private final HashMap<JDA, HashMap<String, AudioPlayerExtender>> playerStorage = new HashMap<>();

    public void checkBotAndGuild(JDA bot, String guildId)
            throws JDANotFound, GuildNotFoundException {
        if (!playerStorage.containsKey(bot)) {
            throw new JDANotFound();
        }
        if (!playerStorage.get(bot).containsKey(guildId)) {
            throw new GuildNotFoundException();
        }
    }

    public void storePlayer(JDA jda, String guildId, AudioPlayerExtender player) {
        playerStorage.computeIfAbsent(jda, k -> new HashMap<>());
        playerStorage.get(jda).put(guildId, player);
    }

    public void removePlayer(JDA jda, String guildId) {
        if (!playerStorage.containsKey(jda)) {
            return;
        }
        if (!playerStorage.get(jda).containsKey(guildId)) {
            return;
        }
        playerStorage.get(jda).remove(guildId);
    }

    public AudioPlayerExtender getPlayer(
            JDA jda,
            String guildId
    ) throws JDANotFound, GuildNotFoundException {
        checkBotAndGuild(jda, guildId);
        return playerStorage.get(jda).get(guildId);
    }

    public static PlayerVault getInstance() { return vaultObj; }
}
