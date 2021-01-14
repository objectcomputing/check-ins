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
  return `${marks.find((mark) => mark.value===value).label}`;
}

const ValueLabelComponent = (props) => (
  <Tooltip arrow open={props.valueLabelDisplay !== "off" && props.open} enterTouchDelay={0} placement="bottom" title={props.value}>
    {props.children}
  </Tooltip>
);

export default function DiscreteSlider({title, lastUsed, onChange, onChangeCommitted}) {
  const classes = useStyles();

  return (
    <div className={classes.root}>
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
