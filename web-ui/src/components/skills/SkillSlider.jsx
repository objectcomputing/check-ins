import React, { useState } from "react";
import { styled } from '@mui/material/styles';
import "./SkillSlider.css";
import {
  Checkbox,
  FormControl,
  FormControlLabel,
  IconButton,
  Tooltip,
} from "@mui/material";
import DeleteIcon from "@mui/icons-material/Delete";
import TextField from "@mui/material/TextField";
import { debounce } from "lodash/function";
import DiscreteSlider from "../slider/Slider";

const PREFIX = 'SkillSlider';
const classes = {
  hidden: `${PREFIX}-hidden`
};

const Root = styled('span')(() => ({
  [`& .${classes.hidden}`]: {
    display: "none",
  }
}));

const SkillSlider = ({
  id,
  description,
  name,
  startLevel,
  lastUsedDate,
  onDelete,
  onUpdate,
  key,
}) => {
  let [currCheck, setCurrCheck] = useState(!lastUsedDate);
  let [lastUsed, setLastUsed] = useState(lastUsedDate);
  let [skillLevel, setSkillLevel] = useState(startLevel);

  const datePickerVisibility = (event) => {
    setCurrCheck(!currCheck);
  };

  const updateLevel = (e, value) => setSkillLevel(value);

  const updateSkillLevel = debounce((event, value) => {
    onUpdate(lastUsed, value, id);
  }, 1500);

  const updateLastUsed = debounce((value) => {
    setLastUsed(value);
    onUpdate(value, skillLevel, id);
  }, 1500);

  const formatDate = (date) => {
    if (!date) {
      return;
    }
    let dateString = date[0] + "-";
    dateString = dateString + (date[1] < 10 ? "0" + date[1] : date[1]) + "-";
    dateString = dateString + (date[2] < 10 ? "0" + date[2] : date[2]);
    return dateString;
  };

  return (
    <>
      <Root>
        <div className="skill-slider">{name}</div>
        <div className="skill-slider-container">
          <DiscreteSlider
            inStartPos={skillLevel}
            onChange={updateLevel}
            onChangeCommitted={updateSkillLevel}
          />
          <IconButton onClick={() => onDelete(id)} size="large">
            <DeleteIcon />
          </IconButton>
        </div>
        {false && (
          <FormControl>
            <FormControlLabel
              control={<Checkbox color="primary" value="current" />}
              label="Currently Used"
              labelPlacement="top"
              checked={currCheck}
              onChange={datePickerVisibility}
            />
          </FormControl>
        )}
        {false && (
          <TextField
            className={currCheck ? classes.hidden : undefined}
            type="date"
            onChange={(event, value) => updateLastUsed(value)}
            defaultValue={formatDate(lastUsed)}
          />
        )}
      </Root>
    </>
  );
};
export default SkillSlider;
