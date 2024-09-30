import java.util.logging.Logger

fun main(args: Array<String>) {
    if (args.isEmpty()) {
        CustomLogger.logger.severe("Missing Command Line Arguments!")
        throw Error("Please provide 'start' as an argument.")
    }
    if (args.first() == "start") {
        val configObject = if (args.size == 2) ConfigObject(args[1] == "beRude") else ConfigObject()
        Controller(View(configObject), Model()).start()
    }
}

class Controller(private val view: View, private val model: Model) {
    private var isDone = false

    private fun toggleLoop() {
        isDone = !isDone
        CustomLogger.logger.info("Loop Stopped")
    }

    fun start() {
        while (!isDone) {
            view.showPreviousGreeter(model.findLastGreetingPair())

            val greeter = view.askForGreeter().also {
                if (it == "done") {
                    toggleLoop()
                    return
                }
            }

            GreetingPair(greeter, view.askForGreeted(greeter)).also {
                model.saveGreetingPair(it)
                view.greet(it)
            }
        }
    }
}

data class ConfigObject(val beRude: Boolean = false)

data class GreetingPair(val greeter: String, val greeted: String)

class View(val configObject: ConfigObject) {
    fun showPreviousGreeter(greetingPair: GreetingPair?) {
        if (greetingPair == null) return

        val (greeter, greeted) = greetingPair
        println("$greeter just greeted $greeted. ${if (configObject.beRude) "Go Away!" else "Does someone else want to greet someone?"}")
    }

    fun askForGreeter(): String {
        println("What is your Name? (type 'done' to quit):")
        return readln()
    }

    fun askForGreeted(greeter: String): String {
        println("Who do you want to greet $greeter?")
        return readln()
    }

    fun greet(greetingPair: GreetingPair) {
        val (greeter, greeted) = greetingPair
        println("$greeter says: 'Hello $greeted!'")
    }
}

class Model {
    fun saveGreetingPair(greetingPair: GreetingPair) = Database.greetingPair.add(greetingPair)

    fun findLastGreetingPair() = try {
        Database.greetingPair.last()
    } catch (error: NoSuchElementException) {
        null
    }
}

object Database {
    val greetingPair = mutableListOf<GreetingPair>()
}

object CustomLogger {
    val logger: Logger = Logger.getLogger(CustomLogger::class.java.name)
}