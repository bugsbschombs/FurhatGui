package furhatos.app.furgui

import furhatos.event.senses.SenseSkillGUIConnected
import furhatos.flow.kotlin.*
import furhatos.gestures.ARKitParams
import furhatos.records.Record
import furhatos.skills.HostedGUI
import furhatos.gestures.Gestures
import furhatos.gestures.defineGesture

// Sarah for log file info
import java.io.File
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.nio.file.Paths
import java.nio.file.Files

//to define new gestures and adjust the mouth
import furhatos.gestures.defineGesture

// Our GUI declaration
val GUI = HostedGUI("ExampleGUI", "assets/exampleGui", PORT)
val VARIABLE_SET = "VariableSet"
val CLICK_BUTTON = "ClickButton"
//Sarah add Slider + InputSaved for textfields
val SLIDER_CHANGE = "SliderChange"
val INPUT_SAVED = "InputSaved"

// Sarah Air Pollution
// character for Air Pollution - Experiment
const val character1 = "SicknessOptimal/SicknessOptimal1"
const val character2 = "AirModerate/AirModerate1"
const val character3 = "AirSensGroups/AirSensGroups2"
const val character4 = "AirUnhealthy/AirUnhealthy2"
const val character5 = "AirUnhealthy/AirUnhealthy3"
const val character6 = "AirUnhealthy/AirUnhealthy4"
const val character7 = "AirHazard/AirHazard2"
const val character8 = "AirHazard/AirHazard3"
const val character9 = "AirHazard/AirHazard4"
const val character10 = "AirHazard/AirHazard5"

// Sarah slider initial
val initialSliderPosition = 0

// track and print time
var sliderStartTime: LocalDateTime? = null
var timeSpentOnGUI: Long = 0

// Track the time and log data
var startTime: LocalDateTime? = null
val logData = mutableListOf<Pair<Int, String>>()

// to log files:
// Add this variable to specify the file name and path
val logFileName = "P01_log.txt"
val logFilePath = "/Users/sschombs/Project_Code/FaceVis/FurhatGui/log_files/$logFileName" // Change to your desired path

// Create a function to log messages to the file
fun logToFile(message: String) {
    val file = File(logFilePath)
    file.appendText("$message\n")
}

// example character for virtual robot
//const val character1 = "Alex"
//const val character2 = "Brooklyn"
//const val character3 = "Chen"
//const val character4 = "Dorothy"
//const val character5 = "Fedora"
//const val character6 = "Fernando"
//const val character7 = "Gyeong"
//const val character8 = "Hayden"
//const val character9 = "Isabel"
//const val character10 = "James"
//const val character11 = "Jamie"
//const val character12 = "Jane"
//const val character13 = "Kione"
//const val character14 = "Lamin"
//const val character15 = "Marty"
//const val character16 = "Maurice"
//const val character17 = "Nazar"
//const val character18 = "Omar"
//const val character19 = "Patricia"
//const val character20 = "Rania"

// define gesture so mouth tilts down
val SadMouth = defineGesture("SadMouth") {
    frame(0.5, persist = true) {
        ARKitParams.MOUTH_FROWN_LEFT to 0.4
        ARKitParams.MOUTH_FROWN_RIGHT to 0.4
    }
}

val SadBrowFrown = defineGesture("SadBrowFrown") {
    frame(0.5, 3.0, persist = true) {
        ARKitParams.BROW_DOWN_LEFT to 0.5
        ARKitParams.BROW_DOWN_RIGHT to 0.5
//        ARKitParams.MOUTH_FROWN_LEFT to 0.5
//        ARKitParams.MOUTH_FROWN_RIGHT to 0.5
    }
}

// Function to save log data to a file
fun saveLogData() {
    val endTime = LocalDateTime.now()
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss")
    val fileName = "slider_log_${endTime.format(formatter)}.txt"
    val logDirectory = Paths.get("/Users/user_name/Project_Code/FaceVis/FurhatTutorials/FurGUI/assets/exampleGui/log_files")

    // Create the logs directory if it doesn't exist
    if (!Files.exists(logDirectory)) {
        Files.createDirectories(logDirectory)
    }

    val file = logDirectory.resolve(fileName).toFile()
    file.printWriter().use { out ->
        out.println("Start Time: ${startTime?.format(formatter)}")
        out.println("End Time: ${endTime.format(formatter)}")
        out.println("Slider Value, Mapped Character")
        logData.forEach { (value, character) ->
            out.println("$value, $character")
        }
    }
}

// Function to map slider value to character
fun getCharacterForSliderValue(value: Int): String {
    return when (value) {
        in 0..8 -> character1
        in 9..34 -> character2
        in 35..54 -> character3
        in 55..77 -> character4
        in 78..101 -> character5
        in 102..124 -> character6
        in 125..149 -> character7
        in 150..174 -> character8
        in 175..199 -> character9
        else -> character10 // For range 200 - 225
    }
}



// Starting state, before our GUI has connected.
val NoGUI: State = state(null) {
    //default is Alex
    onEntry {
        furhat.character = "Alex"
        furhat.attendAll()
    }
    onEvent<SenseSkillGUIConnected> {
        goto(GUIConnected)
    }
}


/*
    Here we know our GUI has connected. Since the user might close down the GUI and then reopen
    again, we inherit our handler from the NoGUI state. An edge case might be that a second GUI
    is opened, but this is not accounted for here.

 */
val GUIConnected = state(NoGUI) {
    onEntry {
        //default character is ALex
        furhat.character = "Alex"
        // Pass data to GUI
        send(DataDelivery(buttons = buttons, inputFields = inputFieldData.keys.toList(), sliderPosition = initialSliderPosition))
    }

    // Users clicked any of our buttons
    onEvent(CLICK_BUTTON) {
        // Directly respond with the value we get from the event, with a fallback
        //furhat.say("You pressed ${it.get("data") ?: "something I'm not aware of" }")

        // Sarah test: Change the character's face when a button is clicked
        val buttonLabel = it.get("data") as String
        when (buttonLabel) {
            "Done" -> {
                sliderStartTime?.let {
                    timeSpentOnGUI = java.time.Duration.between(it, LocalDateTime.now()).seconds
                    val timeMessage = "Time spent on GUI: $timeSpentOnGUI seconds"
                    println(timeMessage)
                    logToFile(timeMessage)
                }
                val doneMessage = "Done: Particulate Matter"
                println(doneMessage) // Print "Done" to the terminal
                logToFile(doneMessage)
//                //track time until done clicked
//                sliderStartTime?.let {
//                    timeSpentOnGUI = java.time.Duration.between(it, LocalDateTime.now()).seconds
//                    println("Time spent on GUI: $timeSpentOnGUI seconds")
//                }
//                println("Done: Cholesterol") // Print "Done" to the terminal
            }
        }

        // Directly respond with the value we get from the event, with a fallback
        //furhat.say("You pressed $buttonLabel")
        // Let the GUI know we're done speaking, to unlock buttons
        send(SPEECH_DONE)
    }

//    onEvent("SliderTimeSpent") {
//        val data = it.get("data") as? Record ?: run {
//            println("No data found in event")
//            return@onEvent
//        }
//
//        val timeSpent = data["timeSpent"] as? Double ?: 0.0
//        println("Time spent on the slider: $timeSpent seconds")
//    }


    //Slider mapping
    // Users changed the slider position
    onEvent(SLIDER_CHANGE) {
        //val sliderValue = (it.get("value") as String).toInt() // Ensure the value is interpreted as an integer
        val sliderValue = it.get("value") as Int
        val character = getCharacterForSliderValue(sliderValue)
        furhat.character = character
        // Add facial expressions
        when (sliderValue) {
            in 0..9 -> furhat.gesture(Gestures.BigSmile(duration = 2.0))
            in 35..54 -> furhat.gesture(SadMouth)
            in 55..124 -> furhat.gesture(SadBrowFrown) //furhat.gesture(Gestures.BrowFrown(duration = 2.0))
            in 125..225 -> furhat.gesture(Gestures.ExpressSad)
        }
        // for log in file
        val logMessage = "Slider value: $sliderValue, Mapped character: $character"
        println(logMessage) // Log the character
        logToFile(logMessage)
        // Set slider start time if it is null
        if (sliderStartTime == null) {
            sliderStartTime = LocalDateTime.now()
        }
        //println("Slider value: $sliderValue, Mapped character: $character") // Log the character
        //furhat.say("You switched face to $character")
        send(SPEECH_DONE)
    }

//    onEvent(VARIABLE_SET) {
//        // Get data from event
//        val data = it.get("data") as Record
//        val variable = data.getString("variable")
//        val value = data.getString("value")
//
//        // Get answer depending on what variable we changed and what the new value is, and speak it out
//        val answer = inputFieldData[variable]?.invoke(value)
//        furhat.say(answer ?: "Something went wrong")
//
//        // Let the GUI know we're done speaking, to unlock buttons
//        send(SPEECH_DONE)
//    }

    onEvent(INPUT_SAVED) {
        //println("Received event INPUT_SAVED: $it") // Debugging log

        val data = it.get("data") as? Record ?: run {
            println("No data found in event")
            return@onEvent
        }

        val label = data["label"] as? String ?: "Unknown Label"
        val inputValue = data["inputValue"] as? String ?: "No Input"
        val logMessage = "User input ($label): $inputValue"
        println(logMessage) // Print the input value to the terminal
        logToFile(logMessage)
        //println("User input ($label): $inputValue") // Print the input value to the terminal
    }

    // Sarah adds data field no answer, just log input text
    onEvent(VARIABLE_SET) {
        // Get data from event
        val data = it.get("data") as Record
        val variable = data.getString("variable")
        val value = data.getString("value")

        // Get answer depending on what variable we changed and what the new value is, and speak it out
        val answer = inputFieldData[variable]?.invoke(value)
        furhat.say(answer ?: "Something went wrong")

        // Let the GUI know we're done speaking, to unlock buttons
        //send(SPEECH_DONE)
    }
}

