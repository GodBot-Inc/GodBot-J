
import com.andreapivetta.kolor.green
import com.andreapivetta.kolor.red
import com.sedmelluq.discord.lavaplayer.jdaudp.NativeAudioSendFactory
import io.github.cdimascio.dotenv.Dotenv
import jdaListeners.BotStateListener
import lib.jda.GeneralListener
import lib.jda.InteractionListener
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.JDABuilder
import net.dv8tion.jda.api.entities.Activity
import net.dv8tion.jda.api.requests.GatewayIntent
import net.dv8tion.jda.api.utils.ChunkingFilter
import net.dv8tion.jda.api.utils.MemberCachePolicy
import net.dv8tion.jda.api.utils.cache.CacheFlag
import singeltons.AudioManagerVault
import singeltons.JDAManager
import javax.security.auth.login.LoginException
import kotlin.system.exitProcess

fun main(args: Array<String>) {
    println("Checking Env...")
    if (!validEnv())
        exitProcess(0)

    val dotenv = Dotenv.load()

    val godBotJDA: JDA
    try {
        godBotJDA = initializeFromToken(dotenv["TOKEN"], dotenv["APPLICATIONID"])
    } catch (e: LoginException) {
        println("Initializing Failed".red())
        exitProcess(0)
    }

    godBotJDA.presence.activity = Activity.playing("music | /help")

    try {
        godBotJDA.awaitReady()
    } catch (e: InterruptedException) {
        println("Bot failed to start".red())
        godBotJDA.shutdown()
        exitProcess(0)
    }
    println("Bot successfully started".green())
}

private fun initializeFromToken(token: String, applicationId: String): JDA {
    val builder = JDABuilder.createDefault(token)
    configureMemoryUsage(builder)
    builder.addEventListeners(
        InteractionListener(),
        BotStateListener(),
        GeneralListener()
    )
    builder.setAudioSendFactory(NativeAudioSendFactory())
    val botInstance = builder.build()
    AudioManagerVault.getInstance().registerJDA(botInstance, botInstance.guilds)
    JDAManager.getInstance().registerJDA(applicationId, botInstance)
    return botInstance
}

private fun configureMemoryUsage(builder: JDABuilder) {
    builder.disableCache(CacheFlag.ACTIVITY)
    builder.setBulkDeleteSplittingEnabled(false)
    builder.disableIntents(
        GatewayIntent.GUILD_PRESENCES,
        GatewayIntent.GUILD_BANS,
        GatewayIntent.GUILD_INVITES,
        GatewayIntent.DIRECT_MESSAGE_TYPING,
        GatewayIntent.DIRECT_MESSAGE_REACTIONS,
        GatewayIntent.GUILD_MESSAGE_REACTIONS,
        GatewayIntent.GUILD_MESSAGE_TYPING
    )
    builder.setMemberCachePolicy(MemberCachePolicy.VOICE)
    builder.setChunkingFilter(ChunkingFilter.NONE)
    builder.setLargeThreshold(100)
}

private fun validEnv(): Boolean {
    val dotenv = Dotenv.load()
    var valid = true

    if (dotenv["APPLICATIONID"] == null) {
        println("Application Id is not given (APPLICATIONID)".red())
        valid = false
    }
    if (dotenv["TOKEN"] == null) {
        println("Token is not given (TOKEN)".red())
        valid = false
    }
    if (dotenv["DBUSERNAME"] == null) {
        println("DB Username is not given (DBUSERNAME)".red())
        valid = false
    }
    if (dotenv["DBPASSWORD"] == null) {
        println("DB Password is not given (DBPASSWORD)".red())
        valid = false
    }
    if (dotenv["YT_API_KEY"] == null) {
        println("Youtube Api Key is not given (YT_API_KEY)".red())
        valid = false
    }
    if (dotenv["SPOT_CLIENT_ID"] == null) {
        println("Spotify Client Id is not given (SPOT_CLIENT_ID)".red())
        valid = false
    }
    if (dotenv["SPOT_CLIENT_SECRET"] == null) {
        println("Spotify Client Secret is not given (SPOT_CLIENT_SECRET)".red())
        valid = false
    }
    if (!valid)
        println("Create a .env file an put it in the root directory (besides the src folder) " +
                "and put in all the tokens listed here: https://github.com/GodBot-Inc/GodBot-J/blob/master/README.md#environment-variables")
    else
        println("Checking Env successful".green())

    return valid
}
