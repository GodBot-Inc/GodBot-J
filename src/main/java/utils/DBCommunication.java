package utils;

import com.mongodb.*;

import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import io.github.cdimascio.dotenv.Dotenv;

import org.bson.Document;
import utils.logging.LoggerContent;

public class DBCommunication {

    private static final DBCommunication dbObj = new DBCommunication();

    private final MongoCollection<Document> queues;
    private final MongoCollection<Document> searches;
    private final MongoCollection<Document> servers;

    private final MongoCollection<Document> commands;
    private final MongoCollection<Document> audioProcesses;
    private final MongoCollection<Document> general;

    private DBCommunication() {
        Dotenv dotenv = Dotenv.load();
        String USERNAME = dotenv.get("DBUSERNAME");
        String PASSWORD = dotenv.get("DBPASSWORD");
        assert USERNAME != null;
        assert PASSWORD != null;

        ConnectionString connectionString = new ConnectionString(
                String.format(
                        "mongodb+srv://%s:%s@cluster0.z4ax5.mongodb.net/myFirstDatabase?retryWrites=true&w=majority",
                        USERNAME,
                        PASSWORD
                )
        );
        MongoClientSettings settings = MongoClientSettings.builder()
                .applyConnectionString(connectionString)
                .build();
        com.mongodb.client.MongoClient mongoClient = MongoClients.create(settings);

        MongoDatabase discordDB = mongoClient.getDatabase("discord");
        MongoDatabase logDB = mongoClient.getDatabase("Logs");

        this.queues = discordDB.getCollection("Queues");
        this.searches = discordDB.getCollection("Searches");
        this.servers = discordDB.getCollection("Servers");

        this.commands = logDB.getCollection("Commands");
        this.audioProcesses = logDB.getCollection("AudioProcesses");
        this.general = logDB.getCollection("General");
    }


    public void generalLog(LoggerContent loggerObj) {
        this.general.insertOne(loggerObj.getDBScheme());
    }

    public void commandLog(LoggerContent loggerObj) {
        this.commands.insertOne(loggerObj.getDBScheme());
    }

    public void audioProcessLog(LoggerContent loggerObj) {
        this.audioProcesses.insertOne(loggerObj.getDBScheme());
    }


    public static DBCommunication getInstance() {
        return dbObj;
    }
}
