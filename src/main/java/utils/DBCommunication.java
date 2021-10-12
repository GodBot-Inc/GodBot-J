package utils;

import com.mongodb.*;
import io.github.cdimascio.dotenv.Dotenv;
import utils.logging.DefaultLoggerClass;

import java.net.UnknownHostException;
import java.util.Map;

public class DBCommunication {

    private static final DBCommunication dbObj = new DBCommunication();
    private final DefaultLoggerClass logger;

    private DBCollection queues;
    private DBCollection searches;
    private DBCollection servers;

    private DBCollection commands;
    private DBCollection audioProcesses;

    private DBCommunication() {
        Dotenv dotenv = Dotenv.load();
        String USERNAME = dotenv.get("DBUSERNAME");
        String PASSWORD = dotenv.get("DBPASSWORD");
        assert USERNAME != null;
        assert PASSWORD != null;

        this.logger = new DefaultLoggerClass(this.getClass().getName() + "Logger");

        MongoClient mongoClient;
        try {
            mongoClient = new MongoClient(
                    new MongoClientURI(
                            String.format(
                                    "mongodb+srv://%s:%s@cluster0.z4ax5.mongodb.net/MyFirstDatabase?retryWrites=true&w=majority",
                                    USERNAME,
                                    PASSWORD
                            )
                    )
            );
        } catch (UnknownHostException e) {
            this.logger.warn("UnknownHostException in Database Communication " + e.getCause());
            return;
        }

        DB discordDB = mongoClient.getDB("discord");
        DB logDB = mongoClient.getDB("Logs");

        assert discordDB != null;
        assert logDB != null;

        this.queues = discordDB.getCollection("Queues");
        this.searches = discordDB.getCollection("Searches");
        this.servers = discordDB.getCollection("Servers");

        assert this.queues != null;
        assert this.searches != null;
        assert this.servers != null;

        this.commands = logDB.getCollection("Commands");
        this.audioProcesses = logDB.getCollection("AudioProcesses");

        assert this.commands != null;
        assert this.audioProcesses != null;
    }


    public void commandLog(LoggerObj loggerObj) {
        this.commands.insert(loggerObj.getDBScheme());
    }

    public void audioProcessLog(LoggerObj loggerObj) {
        this.audioProcesses.insert(loggerObj.getDBScheme());
    }


    public static DBCommunication getInstance() {
        return dbObj;
    }
}
