package discord;

import discord.audio.AudioManagerVault;
import io.github.cdimascio.dotenv.Dotenv;
import discord.listeners.BotStateListener;
import discord.listeners.InteractionListener;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.ChunkingFilter;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import net.dv8tion.jda.api.utils.cache.CacheFlag;

import javax.security.auth.login.LoginException;

public class GodBotSystem {
    public static void main(String[] args) throws LoginException, InterruptedException {
        Dotenv dotenv = Dotenv.load();

        // Load Bot-Token into the program
        String TOKEN = dotenv.get("TOKEN");
        String israTOKEN = dotenv.get("IsrafilTOKEN");
        String APPLICATIONID = dotenv.get("APPLICATIONID");
        String israAPPLICATIONID = dotenv.get("IsrafilAPPLICATIONID");

        assert TOKEN != null;
        assert israTOKEN != null;
        assert APPLICATIONID != null;
        assert israAPPLICATIONID != null;

        JDA godbotJDA = initializeBotFromToken(TOKEN, APPLICATIONID, "godbot", true);
        JDA israJDA = initializeBotFromToken(israTOKEN, israAPPLICATIONID, "israfil", false);

        godbotJDA.getPresence().setActivity(Activity.listening("dope music"));
        israJDA.getPresence().setActivity(Activity.listening("the GodBot System"));

        // Wait until JDA is ready and loaded
        godbotJDA.awaitReady();
        israJDA.awaitReady();
    }

    private static JDA initializeBotFromToken(String TOKEN, String applicationId, String botName, boolean listeners) throws LoginException {
        // Get a builder for the bot, so it can be customized / configured
        JDABuilder builder = JDABuilder.createDefault(TOKEN);

        // Run a configuration so the bot does not use up too much memory
        configureMemoryUsage(builder);

        // Registers all Listeners to the Bot-EventListener
        if (listeners) {
            registerListeners(builder);
        }

        // Create a bot instance
        JDA botInstance = builder.build();

        // Save AudioManagers for every single Guild into the AudioManagerVault
        AudioManagerVault.getInstance().registerJDA(applicationId, botInstance.getGuilds());

        // Save the JDA Object into the JDAManager
        JDAManager.getInstance().registerJDA(botName, botInstance);

        // Return bot Instance
        return botInstance;
    }

    private static void registerListeners(JDABuilder builder) {
        builder.addEventListeners(
                new BotStateListener(),
                new InteractionListener()
        );
    }

    private static void configureMemoryUsage(JDABuilder builder) {
        // Disable cache for member activities (streaming/games/spotify) may be changed later
        builder.disableCache(CacheFlag.ACTIVITY);

        // Enable the bulk delete event
        builder.setBulkDeleteSplittingEnabled(false);

        // Disables Intents for Guild Presences because we don't need it
        builder.disableIntents(GatewayIntent.GUILD_PRESENCES);
        builder.disableIntents(GatewayIntent.GUILD_BANS);
        builder.disableIntents(GatewayIntent.GUILD_INVITES);
        builder.disableIntents(GatewayIntent.GUILD_MESSAGE_TYPING);
        builder.disableIntents(GatewayIntent.GUILD_MESSAGE_REACTIONS);
        builder.disableIntents(GatewayIntent.DIRECT_MESSAGE_REACTIONS);
        builder.disableIntents(GatewayIntent.DIRECT_MESSAGE_TYPING);

        // Only cache members who are in a voice channel
        builder.setMemberCachePolicy(MemberCachePolicy.VOICE);

        // Disable member chunking on startup
        builder.setChunkingFilter(ChunkingFilter.NONE);

        // Consider guilds with more than 100 members as "large".
        // Large guilds will only provide online members in their setup and thus reduce bandwidth if chunking is disabled
        builder.setLargeThreshold(100);
    }
}
