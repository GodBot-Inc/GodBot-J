package functions

import constants.queueFirstEmoji
import constants.queueLastEmoji
import constants.queueLeftEmoji
import constants.queueRightEmoji
import net.dv8tion.jda.api.interactions.components.Button


object QueueButtons {
    fun checkButtons(page: Int, maxPages: Int): ArrayList<Button>? {
        if (maxPages == 1)
            return null
        if (page == maxPages)
            return rightDisabled()
        if (page == 1)
            return leftDisabled()
        return noneDisabled()
    }

    fun leftDisabled(): ArrayList<Button> {
        return arrayListOf(
            Button.secondary("first_disabled", queueFirstEmoji).asDisabled(),
            Button.secondary("left_disabled", queueLeftEmoji).asDisabled(),
            Button.primary("right", queueRightEmoji),
            Button.primary("last", queueLastEmoji)
        )
    }

    fun rightDisabled(): ArrayList<Button> {
        return arrayListOf(
            Button.primary("first", queueFirstEmoji),
            Button.primary("left", queueLeftEmoji),
            Button.secondary("right_disabled", queueRightEmoji).asDisabled(),
            Button.secondary("last_disabled", queueLastEmoji).asDisabled()
        )
    }

    fun allDisabled(): ArrayList<Button> {
        return arrayListOf(
            Button.secondary("first_disabled", queueFirstEmoji).asDisabled(),
            Button.secondary("left_disabled", queueLeftEmoji).asDisabled(),
            Button.secondary("right_disabled", queueRightEmoji).asDisabled(),
            Button.secondary("last_disabled", queueLastEmoji).asDisabled(),
        )
    }

    fun noneDisabled(): ArrayList<Button> {
        return arrayListOf(
            Button.primary("first", queueFirstEmoji),
            Button.primary("left", queueLeftEmoji),
            Button.primary("right", queueRightEmoji),
            Button.primary("last", queueLastEmoji)
        )
    }

}
