package state

import lib.lavaplayer.AudioPlayerExtender

object PlayerStorage {

    private val storage: HashMap<String, AudioPlayerExtender> = HashMap()

    fun store(guildId: String, player: AudioPlayerExtender) = storage.put(guildId, player)
    fun get(guildId: String): AudioPlayerExtender? = storage[guildId]
    fun remove(guildId: String) = storage.remove(guildId)
}
