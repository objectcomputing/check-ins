import React, { useState } from "react";

import "./SkillSlider.css"

import {
    Checkbox,
    FormControl,
    FormControlLabel,
    IconButton
} from '@material-ui/core';
import RemoveIcon from "@material-ui/icons/Remove";
import TextField from '@material-ui/core/TextField';
import ListItem from '@material-ui/core/ListItem';
import { debounce } from "lodash/function";
import { makeStyles } from "@material-ui/core/styles";
import DiscreteSlider from '../slider/Slider'

const useStyles = makeStyles((theme) => ({
  skillRow: {
    justifyContent: "space-around",
  },
  hidden: {
    display: "none",
  }
}));

const SkillSlider = ({id, name, startLevel, lastUsedDate, onDelete, onUpdate, index}) => {
    let [currCheck, setCurrCheck] = useState(!lastUsedDate);
    let [lastUsed, setLastUsed] = useState(lastUsedDate);
    let [skillLevel, setSkillLevel] = useState(startLevel);

    const classes = useStyles();

    const datePickerVisibility = (event) => {
      setCurrCheck(!currCheck);
    };

    const updateSkillLevel = debounce((event, value) => {
        setSkillLevel(value);
        onUpdate(lastUsed, value, index);
    }, 1500);

    const updateDate = debounce((value) => {
        setLastUsed(value);
        onUpdate(value, skillLevel, index);
    }, 1500);

    const formatDate = (date) => {
        if (!date) {
            return;
        }
        let dateString = date[0] + "-";
        dateString = dateString + (date[1] < 10 ? "0" + date[1] : date[1]) + "-";
        dateString = dateString + (date[2] < 10 ? "0" + date[2] : date[2]);
        return dateString;
    }

    return (
      <ListItem className={classes.skillRow}>
        <DiscreteSlider title={name} inStartPos={skillLevel} onChange={updateSkillLevel}/>
        {false && <FormControl>
          <FormControlLabel
            control={<Checkbox color="primary" value="current" />}
            label='Currently Used'
            labelPlacement="top"
            checked={currCheck}
            onChange={datePickerVisibility}
          />
        </FormControl>}
        {false && <TextField
          className={currCheck ? classes.hidden: undefined}
          type="date"
          onChange={(event, value) => updateDate(value)}
          defaultValue={formatDate(lastUsed)}
        />}
        <IconButton onClick={(event) => onDelete(id)}>
          <RemoveIcon />
        </IconButton>
      </ListItem>
    );
};
export default SkillSlider;