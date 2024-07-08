import React, {Component} from 'react'
import FurhatGUI from 'furhat-gui'
import { Grid, Row, Col } from 'react-bootstrap'
import Button from './Button'
import Input from './Input'
import Slider from './Slider'; // Sarah add Slider

class App extends Component {

    constructor(props) {
        super(props)
        this.state = {
          "speaking": false,
          "buttons": [],
          "inputFields": [],
          sliderPosition: 112, // Sarah adds: Initial slider position in the middle
          sliderStartTime: null,
          logMessages: []
        }
        this.furhat = null

    }

    setupSubscriptions() {
        // Our DataDelivery event is getting no custom name and hence gets it's full class name as event name.
        this.furhat.subscribe('furhatos.app.furgui.DataDelivery', (data) => {
            this.setState({
                ...this.state,
                buttons: data.buttons,
                inputFields: data.inputFields,
                sliderPosition: data.sliderPosition || 112 // Sarah adds anchor/slider position
            })
        })

        // This event contains to data so we defined it inline in the flow
        this.furhat.subscribe('SpeechDone', () => {
            this.setState({
                ...this.state,
                speaking: false
            })
        })
    }

    componentDidMount() {
        FurhatGUI()
            .then(connection => {
                this.furhat = connection
                this.setupSubscriptions()
            })
            .catch(console.error)
    }

    clickButton = (button) => {
        // to track time
//        const endTime = new Date();
//        if (button === "Done" && this.state.sliderStartTime) {
//            const timeSpent = (endTime - this.state.sliderStartTime) / 1000; // Time in seconds
//            console.log(`Time spent on the slider: ${timeSpent} seconds`);
//        }
        // to track time end

        this.setState({
            ...this.state,
            speaking: true
        })
        this.furhat.send({
          event_name: "ClickButton",
          data: button
        })
    }

    variableSet = (variable, value) => {
        this.setState({
            ...this.state,
            speaking: true
        })
        this.furhat.send({
          event_name: "VariableSet",
          data: {
            variable,
            value
          }
        })
    }
    //Sarah adds Slider here:
    handleSlide = (value) => {
//    //to track start time
//        const endTime = new Date();
//        if (button === "Done" && this.state.sliderStartTime) {
//            const timeSpent = (endTime - this.state.sliderStartTime) / 1000; // Time in seconds
//            console.log(`Time spent on the slider: ${timeSpent} seconds`);
//
//            this.furhat.send({
//                event_name: "SliderTimeSpent",
//                data: {
//                    timeSpent: timeSpent
//                }
//            });
//        }
    // end track slider start time
         this.setState({
             ...this.state,
             sliderPosition: value,
             speaking: true
         })
         this.furhat.send({
             event_name: "SliderChange",
             value: value
         })
    }

    // safe input from free text field
//    handleSaveInput = (label, inputValue) => {
//        const logMessage = `User input (${label}): ${inputValue}`;
//        this.setState(prevState => ({
//            logMessages: [...prevState.logMessages, logMessage]
//        }))
//        console.log(`User answer: ${inputValue}`);
//        println(`User answer: ${inputValue}`);
//    }

    // Print input from free text field to terminal
    handleSaveInput = (label, inputValue) => {
        console.log(`Sending input data: label=${label}, inputValue=${inputValue}`); // Debugging log
        this.setState({
            ...this.state,
            speaking: false // so you can click button after input field
        });
        this.furhat.send({
            event_name: "InputSaved",
            data: {
                label: label,
                inputValue: inputValue
            }
        });
    }



        render() {
        //console.log('App component is rendering'); // Sarah Add console log to verify rendering
            return (
                <Grid>
                    <Row>
                        <Col sm={12}>
                            <h1></h1>
                        </Col>
                    </Row>
                    <Row>
                        <Col sm={6}>
                            <h2>Particulate Matter (PM)</h2>
                            <p className="text-box">Particulate matter (PM) is an important indicator for today's air pollution. PM refers to breathable particles composed of substances such as sulphate, nitrates, ammonia, black carbon or mineral dust, which can be absorbed through the respiratory system. PM is categorised by particle size, with PM2.5 representing fine particulate matter with a diameter of less than 2.5 µm. Factors such as combustion of fuels in power plants, industries or vehicles can increase the level of PM2.5.</p>
                        </Col>
                        <Col sm={6}>
                            <h2>Data Explorer</h2>
                            <p> Please take 2-3 minutes to explore the different levels of PM2.5, using the slider below. The data will be presented visually.</p>
                            <Slider onSlide={this.handleSlide} initialValue={this.state.sliderPosition} />
                            <div className="input-wrapper">
                                <p> Please answer the following questions:</p>
                                <div className="input-wrapper">
                                    <Input label="Looking at the visualisation, please describe in your own words the amount of particulate matter represented by a slider value of 20 μg/m³? How do you interpret this level of PM2.5?" onSave={this.handleSaveInput} />
                                    <div>
                                        {this.state.logMessages.map((message, index) => (
                                            <p key={index}>{message}</p>
                                        ))}
                                    </div>
                                </div>
                            </div>
                            <div className="input-wrapper">
                                <Input label="Comparing the visualisation at slider values of 20 μg/m³ and 80 μg/m³, what differences do you notice, and what do these differences tell you about the corresponding levels of PM2.5?" onSave={this.handleSaveInput} />
                                <div>
                                    {this.state.logMessages.map((message, index) => (
                                        <p key={index}>{message}</p>
                                    ))}
                                </div>
                            </div>
                            <Input label="How would you feel when the PM2.5 levels extend 125 μg/m³ in your city?" onSave={this.handleSaveInput} />
                            <div>
                                {this.state.logMessages.map((message, index) => (
                                    <p key={index}>{message}</p>
                                ))}
                            </div>
                            <div className="button-wrapper">
                                <Button label="Done" speaking={this.state.speaking} onClick={this.clickButton} />
                            </div>
                        </Col>
                    </Row>
                </Grid>
            )
        }
    }

//    render() {
//    //console.log('App component is rendering'); // Sarah Add console log to verify rendering
//        return (
//            <Grid>
//                <Row>
//                    <Col sm={12}>
//                        <h1>Air Pollution</h1>
//                    </Col>
//                </Row>
//                <Row>
//                    <Col sm={6}>
//                        <h2>TEST</h2>
//                        {this.state.buttons.map((label) =>
//                            <Button key={label} label={label} onClick={this.clickButton} speaking={this.state.speaking} />
//                        )}
//                    </Col>
//                    <Col sm={6}>
//                        <h2>TEST TEST TEST</h2>
//                        {this.state.inputFields.map((label) =>
//                            <Input key={label} label={label} onSave={this.variableSet} speaking={this.state.speaking} />
//                        )}
//                        <h2>Particulate matter (PM)</h2>
//                        <p>PM is a common proxy indicator for air pollution.</p>
//                        <Slider onSlide={this.handleSlide} initialValue={this.state.sliderPosition} />
//                    </Col>
//                </Row>
//            </Grid>
//        )
//    }
//}

export default App;
