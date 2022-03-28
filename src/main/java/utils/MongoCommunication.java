package utils;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import io.github.cdimascio.dotenv.Dotenv;
import org.bson.Document;
import org.jetbrains.annotations.Nullable;

public class MongoCommunication {

    private static final MongoCommunication dbObj = new MongoCommunication();

    private final MongoCollection<Document> queues;
    private final MongoCollection<Document> searches;
    private final MongoCollection<Document> servers;

    private MongoCommunication() {
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
    }

    public void addQueue(Document document) {
        if (this.queues.countDocuments(document) > 0) {
            return;
        }
        this.queues.insertOne(document);
    }

    public void setCurrentQueuePage(String messageId, int page) {
        this.queues.updateOne(Filters.eq("messageId", messageId), new Document("$set", new Document("currentPage", page)));
    }

    @Nullable public Document getQueue(String messageId) {
        return this.queues.find(Filters.eq("messageId", messageId)).first();
    }

    public void deleteQueue(String messageId) {
        this.queues.deleteOne(Filters.eq("messageId", messageId));
    }

    public static MongoCommunication getInstance() {
        return dbObj;
    }
}
