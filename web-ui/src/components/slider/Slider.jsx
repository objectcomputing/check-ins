import React from 'react';
import { makeStyles } from '@material-ui/core/styles';
import Typography from '@material-ui/core/Typography';
import Slider from '@material-ui/core/Slider';
import Tooltip from '@material-ui/core/Tooltip';

const useStyles = makeStyles({
  root: {
    width: 300,
  },
});

const marks = [
  {
    value: 0,
    label: 'Beginner',
  },
  {
    value: 25,
    label: 'Easy',
  },
  {
    value: 50,
    label: 'Medium',
  },
  {
    value: 75,
    label: 'Hard',
  },
  {
    value: 100,
    label: 'Expert'
  }
];

function valuetext(value) {
  return `${value}°C`;
}

function valueLabelFormat(value) {
  const index = marks.findIndex((mark) => mark.value === value);
  const mark = marks[index];
  return mark && mark.label;
}

const ValueLabelComponent = (props) => (
  <Tooltip open={props.open} enterTouchDelay={0} placement="top" title={props.value}>
    {props.children}
  </Tooltip>
);

export default function DiscreteSlider() {
  const classes = useStyles();

  return (
    <div className={classes.root}>
      <Typography id="discrete-slider-restrict" gutterBottom>
        How good are you?
      </Typography>
      <Slider
        defaultValue={50}
        valueLabelFormat={valueLabelFormat}
        ValueLabelComponent={ValueLabelComponent}
        getAriaValueText={valuetext}
        aria-labelledby="discrete-slider-restrict"
        step={null}
        valueLabelDisplay="on"
        marks={marks}
      />
    </div>
  );
}