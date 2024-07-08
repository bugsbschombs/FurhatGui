import React, { Component } from 'react';
import { Col } from 'react-bootstrap';

class Slider extends Component {
  constructor(props) {
    super(props);
    this.state = {
      value: props.initialValue || 112,
    };
  }

  handleChange = (event) => {
    const newValue = parseInt(event.target.value, 10); // Ensure the value is an integer
    this.setState({ value: newValue });
    console.log('Slider value changed:', newValue); // Debugging log
  }

  handleMouseUp = () => {
    this.props.onSlide(this.state.value);
    console.log('Slider value set:', this.state.value); // Debugging log
  }



  render() {
    console.log('Slider component is rendering'); // Debugging log
    const { value } = this.state;
    return (
      <Col sm={12} className={"sliderContainer"} style={{ padding: '10px', marginTop: '20px' }}>
        <div style={{ display: 'flex', justifyContent: 'space-between' }}>
           <span>0 μg/m3</span>
           <span>225 μg/m3</span>
        </div>
        <input
          type="range"
          min="0"
          max="225" // 225 for experiment with Air Pollution
          value={value}
          onChange={this.handleChange}
          onMouseUp={this.handleMouseUp}
          style={{
            width: '100%'
          }} // Ensure the slider takes the full width
        />
        <div style={{ textAlign: 'center', marginTop: '10px' }}>
          Current value: {value} μg/m3
        </div>
        <div style={{ height: '20px' }}>
        </div>
      </Col>
    );
  }
}

export default Slider;

