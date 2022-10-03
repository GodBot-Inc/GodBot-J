package lib

import com.mongodb.ConnectionString
import com.mongodb.MongoClientSettings
import com.mongodb.client.MongoClients
import com.mongodb.client.MongoCollection
import com.mongodb.client.model.Filters
import io.github.cdimascio.dotenv.Dotenv
import org.bson.Document

object Mongo {

    private val configDB: MongoCollection<Document>

    init {
        val dotenv = Dotenv.load()
        val username = dotenv["DBUSERNAME"]
        val password = dotenv["DBPASSWORD"]
        val connectionString = ConnectionString(
            "mongodb+srv://$username:$password@cluster0.z4ax5.mongodb.net/test"
        )
        val settings = MongoClientSettings.builder()
            .applyConnectionString(connectionString)
            .build()
        val client = MongoClients.create(settings)
        val discordDB = client.getDatabase("discord")
        configDB = discordDB.getCollection("Config")
    }

    fun getMessageDeletionTime(serverId: String?): Int? {
        if (serverId == null)
            return null
        return configDB.find(Filters.eq("serverId", serverId)).first()?.getInteger("messageDeletionTime")
    }

    fun setMessageDeletionTime(serverId: String, deletionTime: Int) {
        val result = configDB.updateOne(
            Filters.eq("serverId", serverId),
            Document("\$set", Document("messageDeletionTime", deletionTime))
        )
        if (result.modifiedCount == 0L)
            configDB.insertOne(Document().append("serverId", serverId).append("messageDeletionTime", deletionTime))
    }
}
