import com.sedmelluq.discord.lavaplayer.jdaudp.NativeAudioSendFactory;
import io.github.cdimascio.dotenv.Dotenv;
import jdaListeners.BotStateListener;
import jdaListeners.GeneralListener;
import jdaListeners.InteractionListener;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.ChunkingFilter;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import net.dv8tion.jda.api.utils.cache.CacheFlag;
import singeltons.AudioManagerVault;
import singeltons.JDAManager;

import javax.security.auth.login.LoginException;

public class GodBotSystem {
    public static void main(String[] args) {
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

        JDA godbotJDA;
        JDA israJDA;
        try {
            godbotJDA = initializeBotFromToken(TOKEN, APPLICATIONID, true);
            israJDA = initializeBotFromToken(israTOKEN, israAPPLICATIONID, false);
        } catch (LoginException e) {
            e.printStackTrace();
            return;
        }

        israJDA.getPresence().setActivity(Activity.listening("the GodBot System"));

        // Wait until JDA is ready and loaded
        try {
            godbotJDA.awaitReady();
            israJDA.awaitReady();
        } catch (InterruptedException e) {
            e.printStackTrace();
            godbotJDA.shutdown();
            israJDA.shutdown();
        }
    }

    private static JDA initializeBotFromToken(String TOKEN, String applicationId, boolean listeners)
            throws LoginException {
        // Get a builder for the bot, so it can be customized / configured
//        DefaultShardManagerBuilder builder = DefaultShardManagerBuilder.createDefault(
//                TOKEN,
//                GatewayIntent.GUILD_VOICE_STATES,
//                GatewayIntent.GUILD_PRESENCES,
//                GatewayIntent.GUILD_MEMBERS,
//                GatewayIntent.GUILD_MESSAGES,
//                GatewayIntent.GUILD_WEBHOOKS)
//                .setMemberCachePolicy(MemberCachePolicy.VOICE)
//                .disableCache(
//                        CacheFlag.ACTIVITY,
//                        CacheFlag.CLIENT_STATUS,
//                        CacheFlag.EMOTE,
//                        CacheFlag.ROLE_TAGS,
//                        CacheFlag.ONLINE_STATUS
//                )
//                .setBulkDeleteSplittingEnabled(false);

        JDABuilder builder = JDABuilder.createDefault(TOKEN);

        // Run a configuration so the bot does not use up too much memory
        configureMemoryUsage(builder);

        // Registers all Listeners to the Bot-EventListener
        if (listeners) {
            builder.addEventListeners(
                    new InteractionListener(),
                    new BotStateListener(),
                    new GeneralListener()
            );
        }

        // get jda default audio send factory
        builder.setAudioSendFactory(new NativeAudioSendFactory());

        // Create a bot instance
        JDA botInstance = builder.build();

        // Save AudioManagers for every single Guild into the AudioManagerVault
        AudioManagerVault.getInstance().registerJDA(botInstance, botInstance.getGuilds());

        // Save the JDA Object into the JDAManager
        JDAManager.getInstance().registerJDA(applicationId, botInstance);

        // Return bot Instance
        return botInstance;
    }

    private static void registerListeners(JDABuilder builder) {
        builder.addEventListeners(
                new BotStateListener(),
                new InteractionListener(),
                new GeneralListener()
        );
    }

    private static void configureMemoryUsage(JDABuilder builder) {
        // Disable cache for member activities (streaming/games/spotify) may be changed later
        builder.disableCache(CacheFlag.ACTIVITY);

        // Enable the bulk delete event
        builder.setBulkDeleteSplittingEnabled(false);

        // Disables Intents for Guild Presences because we don't need it
        builder.disableIntents(
                GatewayIntent.GUILD_PRESENCES,
                GatewayIntent.GUILD_BANS,
                GatewayIntent.GUILD_INVITES,
                GatewayIntent.DIRECT_MESSAGE_TYPING,
                GatewayIntent.DIRECT_MESSAGE_REACTIONS,
                GatewayIntent.GUILD_MESSAGE_REACTIONS,
                GatewayIntent.GUILD_MESSAGE_TYPING
        );

        // Only cache members who are in a voice channel
        builder.setMemberCachePolicy(MemberCachePolicy.VOICE);

        // Disable member chunking on startup
        builder.setChunkingFilter(ChunkingFilter.NONE);

        // Consider guilds with more than 100 members as "large".
        // Large guilds will only provide online members in their setup and thus reduce bandwidth if chunking is disabled
        builder.setLargeThreshold(100);
    }
}
