package listeners;

import net.dv8tion.jda.api.events.ExceptionEvent;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.events.ReconnectedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

public class BotStateListener extends ListenerAdapter {
    public void onReady(@NotNull ReadyEvent event) {
        System.out.println("Client is ready and loaded");
    }

    public void onDisconnect(@NotNull ReconnectedEvent event) {

    }
}
