package singeltons;

import net.dv8tion.jda.api.JDA;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;

public class JDAManager {

    private static final JDAManager managerObj = new JDAManager();

    //               ApplicationId BotJDA
    private final HashMap<String, JDA> JDAStorage = new HashMap<>();

    public void registerJDA(String applicationId, JDA jda) {
        JDAStorage.put(applicationId, jda);
    }

    @Nullable
    public JDA getJDA(String applicationId) {
        if (!JDAStorage.containsKey(applicationId)) {
            return null;
        }
        return JDAStorage.get(applicationId);
    }

    public static JDAManager getInstance() {
        return managerObj;
    }
}
