import React, { useState } from "react";

import "./SkillSlider.css"

import {
    Checkbox,
    FormLabel,
    FormControl,
    FormGroup,
    FormControlLabel,
    FormHelperText,
    IconButton } from '@material-ui/core';
import SaveIcon from "@material-ui/icons/Done";
import RemoveIcon from "@material-ui/icons/Remove";
import TextField from '@material-ui/core/TextField';

import DiscreteSlider from '../slider/Slider'

const SkillSlider = ({id, name, startLevel, lastUsedDate, onDelete, onUpdate, index}) => {
    let [currCheck, setCurrCheck] = useState(!lastUsedDate);
    let [lastUsed, setLastUsed] = useState(lastUsedDate);
    let [skillLevel, setSkillLevel] = useState(startLevel);

    const datePickerVisibility = (event) => {
      setCurrCheck(!currCheck);
    };

    const updateSkillLevel = (event, value) => {
        setSkillLevel(value);
    };

    const updateDate = (event) => {
        setLastUsed(event.target.value);
    }

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
      <div className="slider-row">
        <DiscreteSlider title={name} inStartPos={skillLevel} onChange={updateSkillLevel}/>
        <FormControl>
          <FormControlLabel
            control={<Checkbox value="current" />}
            label='Skill currently in use'
            labelPlacement="top"
            checked={currCheck}
            onChange={datePickerVisibility}
          />
          <TextField
            className={currCheck ? 'date-box-hidden' : 'date-box'}
            type="date"
            onChange={(event) => updateDate(event)}
            defaultValue={formatDate(lastUsed)}
          />
        </FormControl>
        <IconButton onClick={(event) => onUpdate(lastUsed, skillLevel, index)}>
          <SaveIcon />
        </IconButton>
        <IconButton onClick={(event) => onDelete(id)}>
          <RemoveIcon />
        </IconButton>
      </div>
    );
};
export default SkillSlider;