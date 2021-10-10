import io.github.cdimascio.dotenv.Dotenv;
import listeners.BotStateListener;
import listeners.InteractionListener;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.ChunkingFilter;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import net.dv8tion.jda.api.utils.cache.CacheFlag;

import javax.security.auth.login.LoginException;
import java.io.IOException;

public class GodBotSystem {
    public static void main(String[] args) throws LoginException, InterruptedException, IOException {
        Dotenv dotenv = Dotenv.load();

        // Load Bot-Token into the program
        String TOKEN = dotenv.get("TOKEN");
        String israTOKEN = dotenv.get("IsrafilTOKEN");

        JDA godbotJda = initializeBotFromToken(TOKEN, true);
        JDA israJDA = initializeBotFromToken(israTOKEN, false);

        // TODO: Initialize AudioManager for each guild for every bot and store them in the AudioManagerManager
        // TODO: Logger -> log into database

        godbotJda.getPresence().setActivity(Activity.listening("dope music"));
        israJDA.getPresence().setActivity(Activity.listening("the GodBot System"));

        // Wait until JDA is ready and loaded
        godbotJda.awaitReady();
        israJDA.awaitReady();
    }

    private static JDA initializeBotFromToken(String TOKEN, boolean listeners) throws LoginException {
        // Get a builder for the bot, so it can be customized / configured
        JDABuilder builder = JDABuilder.createDefault(TOKEN);

        // Run a configuration so the bot does not use up too much memory
        configureMemoryUsage(builder);

        // Initialize Listener Logger for listeners
        ListenerLogger logger = ListenerLogger.getLogger();

        // Registers all Listeners to the Bot-EventListener
        if (listeners) {
            registerListeners(builder, logger);
        }

        // Create bot instance
        return builder.build();
    }

    private static void registerListeners(JDABuilder builder, ListenerLogger logger) {
        builder.addEventListeners(
                new BotStateListener(logger),
                new InteractionListener(logger)
        );
    }

    private static void configureMemoryUsage(JDABuilder builder) {
        // Disable cache for member activities (streaming/games/spotify) may be changed later
        builder.disableCache(CacheFlag.ACTIVITY);

        // Enable the bulk delete event
        builder.setBulkDeleteSplittingEnabled(false);

        // Disables Intents for Guild Presences because we don't need it
        builder.disableIntents(GatewayIntent.GUILD_PRESENCES);

        // Only cache members who are either in a voice channel
        builder.setMemberCachePolicy(MemberCachePolicy.VOICE);

        // Disable member chunking on startup
        builder.setChunkingFilter(ChunkingFilter.NONE);

        // Consider guilds with more than 100 members as "large".
        // Large guilds will only provide online members in their setup and thus reduce bandwidth if chunking is disabled
        builder.setLargeThreshold(100);
    }
}
