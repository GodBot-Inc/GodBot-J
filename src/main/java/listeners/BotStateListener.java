package listeners;

import net.dv8tion.jda.api.events.ExceptionEvent;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.events.ReconnectedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import utils.loggers.ListenerLogger;

public class BotStateListener extends ListenerAdapter {

    ListenerLogger logger;

    public BotStateListener(ListenerLogger logger) {
        this.logger = logger;
    }

    public void onReady(@NotNull ReadyEvent event) {
        this.logger.log("Client is ready and loaded");
        System.out.println("Client is ready and loaded");
    }

    public void onException(@NotNull ExceptionEvent event) {
        this.logger.warn("" + event.getCause());
    }

    public void onDisconnect(@NotNull ReconnectedEvent event) {
        this.logger.warn("Disconnected from channel " + event.getResponseNumber());
    }
}
