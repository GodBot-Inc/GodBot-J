package features

import ktCommands.queue.objects.ButtonEventWrapper


object ButtonDistributor {
    private val buttonMessages = HashMap<String, (ButtonEventWrapper) -> Unit>()

    fun distribute(event: ButtonEventWrapper) {
        for ((key, value) in buttonMessages) {
            print("key $key eventId: ${event.messageId}")
            if (key == event.messageId) {
                value(event)
            }
        }
    }

    fun add(id: String, func: (ButtonEventWrapper) -> Unit) {
        println("Added Button Distributor $id $func")
        buttonMessages[id] = func
    }

    fun remove(id: String) {
        buttonMessages.remove(id)
    }

}
