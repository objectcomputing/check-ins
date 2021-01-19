import React from 'react';
import Typography from '@material-ui/core/Typography';
import Slider from '@material-ui/core/Slider';
import Tooltip from '@material-ui/core/Tooltip';
import './Slider.css';

const marks = [
  {
    value: 1,
    label: 'Beginner',
  },
  {
    value: 2,
    label: 'Easy',
  },
  {
    value: 3,
    label: 'Medium',
  },
  {
    value: 4,
    label: 'Hard',
  },
  {
    value: 5,
    label: 'Expert'
  }
];

function valuetext(value) {
  return `${marks.find((mark) => mark.value===value)?.label}`;
}

const ValueLabelComponent = (props) => (
  <Tooltip arrow open={props.valueLabelDisplay !== "off" && props.open} enterTouchDelay={0} placement="bottom" title={props.value ? props.value:""}>
    {props.children}
  </Tooltip>
);

export default function DiscreteSlider({title, lastUsed, onChange, onChangeCommitted}) {

  return (
    <div className="skill-slider">
      <Typography id="discrete-slider-restrict" gutterBottom>
        {title}
      </Typography>
      <Slider
        min={1}
        max={5}
        defaultValue={3}
        valueLabelFormat={() => lastUsed}
        ValueLabelComponent={ValueLabelComponent}
        getAriaValueText={valuetext}
        aria-labelledby="discrete-slider-restrict"
        step={null}
        valueLabelDisplay={lastUsed ? 'on' : 'off'}
        marks={marks}
        onChange={onChange}
        onChangeCommitted={onChangeCommitted}
      />
    </div>
  );
}
