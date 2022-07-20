package utils;

import io.github.cdimascio.dotenv.Dotenv;
import ktUtils.ENVCheckFailedException;

public class Checks {
    public static void checkENV() throws ENVCheckFailedException {
        Dotenv dotenv = Dotenv.load();

        // Main Bot Check
        if (dotenv.get("APPLICATIONID") == null)
            throw new ENVCheckFailedException("APPLICATIONID is missing");
        if (dotenv.get("TOKEN") == null)
            throw new ENVCheckFailedException("TOKEN is missing");

        // Angel Check
        if (dotenv.get("IsrafilAPPLICATIONID") == null)
            throw new ENVCheckFailedException("IsrafilAPPLICATIONID is missing");

        if (dotenv.get("IsrafilTOKEN") == null)
            throw new ENVCheckFailedException("IsrafilTOKEN is missing");

        // DB Check
        if (dotenv.get("DBUSERNAME") == null)
            throw new ENVCheckFailedException("DBUSERNAME is missing (MongoDB)");
        if (dotenv.get("DBPASSWORD") == null)
            throw new ENVCheckFailedException("DBPASSWORD is missing (MongoDB)");

        // YT API Check
        if (dotenv.get("YT_API_KEY") == null)
            throw new ENVCheckFailedException("YT_API_KEY is missing");

        // Spotify API
        if (dotenv.get("SPOT_CLIENT_ID") == null)
            throw new ENVCheckFailedException("SPOT_CLIENT_ID is missing");
        if (dotenv.get("SPOT_CLIENT_SECRET") == null)
            throw new ENVCheckFailedException("SPOT_CLIENT_SECRET");
    }
}
