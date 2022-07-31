package state

import objects.AudioPlayerExtender

object PlayerStorage {

    private val storage: HashMap<String, AudioPlayerExtender> = HashMap()

    fun store(guildId: String, player: AudioPlayerExtender) = storage.put(guildId, player)
    fun get(guildId: String): AudioPlayerExtender? = storage[guildId]
    fun actionOnEveryPlayer(func: (AudioPlayerExtender) -> Unit) {
        for ((_, value) in storage) {
            func(value)
        }
    }
    fun remove(guildId: String) = storage.remove(guildId)
}
