import { debounce } from 'lodash/function';
import React, { useState } from 'react';
import DeleteIcon from '@mui/icons-material/Delete';
import {
  Checkbox,
  FormControl,
  FormControlLabel,
  IconButton,
  TextField,
  Typography
} from '@mui/material';
import RadioGroup from '@mui/material/RadioGroup';
import Radio from '@mui/material/Radio';
import { styled } from '@mui/material/styles';

import './SkillLevel.css';

const PREFIX = 'SkillLevel';
const classes = {
  hidden: `${PREFIX}-hidden`
};

const Root = styled('span')(() => ({
  [`& .${classes.hidden}`]: {
    display: 'none'
  },
  '@media screen and (max-width: 900px)': {
    width: '100%'
  }
}));

const SkillLevel = ({
  id,
  name,
  startLevel,
  lastUsedDate,
  onDelete,
  onUpdate
}) => {
  let [currCheck, setCurrCheck] = useState(!lastUsedDate);
  let [lastUsed, setLastUsed] = useState(lastUsedDate);
  let [skillLevel, setSkillLevel] = useState(startLevel);

  const datePickerVisibility = () => {
    setCurrCheck(!currCheck);
  };

  const updateLevel = (e, value) => {
    setSkillLevel(value);
    updateSkillLevel(e, value);
  }

  const updateSkillLevel = debounce((event, value) => {
    onUpdate(lastUsed, value, id);
  }, 1500);

  const updateLastUsed = debounce(value => {
    setLastUsed(value);
    onUpdate(value, skillLevel, id);
  }, 1500);

  const formatDate = date => {
    if (!date) return;
    let dateString = date[0] + '-';
    dateString = dateString + (date[1] < 10 ? '0' + date[1] : date[1]) + '-';
    dateString = dateString + (date[2] < 10 ? '0' + date[2] : date[2]);
    return dateString;
  };

  return (
    <>
      <Root>
        <Typography variant="body1">
          {name}
        </Typography>
        <div className="skill-level-container">
          <RadioGroup
            row
            value={skillLevel}
            onChange={updateLevel}
          >
            <FormControlLabel value="0" control={<Radio/>} label="None"/>
            <FormControlLabel value="1" control={<Radio/>} label="Novice"/>
            <FormControlLabel value="2" control={<Radio/>} label="Practitioner"/>
            <FormControlLabel value="3" control={<Radio/>} label="Expert"/>
          </RadioGroup>
          <IconButton onClick={() => onDelete(id)} size="large">
            <DeleteIcon />
          </IconButton>
        </div>
      </Root>
    </>
  );
};
export default SkillLevel;
