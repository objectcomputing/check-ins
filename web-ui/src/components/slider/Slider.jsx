import React from 'react';
import Typography from '@material-ui/core/Typography';
import Slider from '@material-ui/core/Slider';
import Tooltip from '@material-ui/core/Tooltip';
import './Slider.css';


const DiscreteSlider = ({title, onChange, onChangeCommitted, inMarks, inStartPos}) => {

  const defaultMarks = [
    {
      value: 1,
      label: 'Interested',
    },
    {
      value: 2,
      label: 'Novice',
    },
    {
      value: 3,
      label: 'Intermediate',
    },
    {
      value: 4,
      label: 'Advanced',
    },
    {
      value: 5,
      label: 'Expert'
    },
  ];

  function valuetext(value) {
    return `${value}`;
  }

  const ValueLabelComponent = (props) => (
    <Tooltip arrow open={props.valueLabelDisplay !== "off" && props.open} enterTouchDelay={0} placement="bottom" title={props.value ? props.value:""}>
      {props.children}
    </Tooltip>
  );

  const marks = inMarks ? inMarks : defaultMarks;
  const startPos = inStartPos ? inStartPos : Math.ceil(marks.length/2);

  return (
    <div className="skill-slider">
      <Typography id="discrete-slider-restrict" gutterBottom>
        {title}
      </Typography>
      <Slider
        min={0.5}
        max={marks.length+.5}
        defaultValue={startPos}
        ValueLabelComponent={ValueLabelComponent}
        getAriaValueText={valuetext}
        step={null}
        marks={marks}
        onChange={onChange}
        onChangeCommitted={onChangeCommitted}
      />
    </div>
  );
};

export default DiscreteSlider;