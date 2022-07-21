package singeltons;

import net.dv8tion.jda.api.JDA;

import java.util.HashMap;

public class JDAManager {

    private static final JDAManager managerObj = new JDAManager();

    //               ApplicationId BotJDA
    private final HashMap<String, JDA> JDAStorage = new HashMap<>();

    public void registerJDA(String applicationId, JDA jda) {
        JDAStorage.put(applicationId, jda);
    }

    public JDA getJDA(String applicationId) {
        return JDAStorage.get(applicationId);
    }

    public static JDAManager getInstance() {
        return managerObj;
    }
}
