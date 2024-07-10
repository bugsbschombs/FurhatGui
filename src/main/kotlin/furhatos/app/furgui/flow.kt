package furhatos.app.furgui

import furhatos.event.senses.SenseSkillGUIConnected
import furhatos.flow.kotlin.*
import furhatos.records.Record
import furhatos.skills.HostedGUI
import furhatos.gestures.Gestures

// Sarah for log file info
import java.io.File
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.nio.file.Paths
import java.nio.file.Files

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
val initialSliderPosition = 112

// track and print time
var sliderStartTime: LocalDateTime? = null
var timeSpentOnGUI: Long = 0

// Track the time and log data
var startTime: LocalDateTime? = null
val logData = mutableListOf<Pair<Int, String>>()


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

//Example for granualr slider real or virtual robot
//fun getCharacterForSliderValue(value: Int): String {
//    return when (value) {
//        in 0..4 -> character1
//        in 5..9 -> character2
//        in 10..14 -> character3
//        in 15..19 -> character4
//        in 20..24 -> character5
//        in 25..29 -> character6
//        in 30..34 -> character7
//        in 35..39 -> character8
//        in 40..44 -> character9
//        in 45..49 -> character10
//        in 50..54 -> character11
//        in 55..59 -> character12
//        in 60..64 -> character13
//        in 65..69 -> character14
//        in 70..74 -> character15
//        in 75..79 -> character16
//        in 80..84 -> character17
//        in 85..89 -> character18
//        in 90..94 -> character19
//        else -> character20 // For range 95-100
//    }
//}


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
                //track time until done clicked
                sliderStartTime?.let {
                    timeSpentOnGUI = java.time.Duration.between(it, LocalDateTime.now()).seconds
                    println("Time spent on GUI: $timeSpentOnGUI seconds")
                }
                println("Done: Cholesterol") // Print "Done" to the terminal
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
            in 55..124 -> furhat.gesture(Gestures.BrowFrown)
            in 125..225 -> furhat.gesture(Gestures.ExpressSad)
        }
        // Set slider start time if it is null
        if (sliderStartTime == null) {
            sliderStartTime = LocalDateTime.now()
        }
        println("Slider value: $sliderValue, Mapped character: $character") // Log the character
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

        println("User input ($label): $inputValue") // Print the input value to the terminal
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

