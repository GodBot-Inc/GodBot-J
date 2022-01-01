package commands;

import io.github.cdimascio.dotenv.Dotenv;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import org.jetbrains.annotations.NotNull;

public interface Command {

    Dotenv dotenv = Dotenv.load();
    String applicationId = dotenv.get("APPLICATIONID");

    static void trigger(@NotNull SlashCommandEvent event) {}
}
