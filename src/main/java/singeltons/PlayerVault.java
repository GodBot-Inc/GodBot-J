package singeltons;

import ktUtils.AudioPlayerExtender;
import ktUtils.GuildNotFoundException;
import ktUtils.JDANotFoundException;
import net.dv8tion.jda.api.JDA;

import java.util.HashMap;
import java.util.Map;


public class PlayerVault {

    private static final PlayerVault vaultObj = new PlayerVault();
    //                    Bot         GuildId
    private final HashMap<JDA, HashMap<String, AudioPlayerExtender>> playerStorage = new HashMap<>();

    public void checkBotAndGuild(JDA bot, String guildId)
            throws JDANotFoundException, GuildNotFoundException {
        if (!playerStorage.containsKey(bot)) {
            throw new JDANotFoundException();
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

    public void removePlayer(AudioPlayerExtender playerExtender) {
        // Time Complexity: n^2
        for (Map.Entry<JDA, HashMap<String, AudioPlayerExtender>> entry : playerStorage.entrySet()) {
            for (Map.Entry<String, AudioPlayerExtender> entry2 : entry.getValue().entrySet()) {
                if (entry2.getValue() == playerExtender) {
                    playerStorage.get(entry.getKey()).remove(entry2.getKey());
                }
            }
        }
    }

    public AudioPlayerExtender getPlayer(JDA jda, String guildId) {
        try {
            checkBotAndGuild(jda, guildId);
        } catch(JDANotFoundException | GuildNotFoundException e) {
            return null;
        }
        return playerStorage.get(jda).get(guildId);
    }

    public static PlayerVault getInstance() { return vaultObj; }
}
