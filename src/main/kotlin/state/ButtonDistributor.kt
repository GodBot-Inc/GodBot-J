package state

import lib.jda.ButtonEventWrapper


object ButtonDistributor {
    private val buttonMessages = HashMap<String, (ButtonEventWrapper) -> Unit>()

    fun distribute(event: ButtonEventWrapper) {
        for ((key, value) in buttonMessages) {
            if (key == event.messageId) {
                value(event)
            }
        }
    }

    fun add(id: String, func: (ButtonEventWrapper) -> Unit) {
        buttonMessages[id] = func
    }

    fun remove(id: String) {
        buttonMessages.remove(id)
    }

}
