import React, {useCallback} from "react";
import PropTypes from "prop-types";
import TextField from "@mui/material/TextField";
import RadioGroup from "@mui/material/RadioGroup";
import {FormControlLabel} from "@mui/material";
import Radio from "@mui/material/Radio";
import Slider from "@mui/material/Slider";
import withStyles from "@mui/styles/withStyles";

const agreeMarks = ["Strongly Disagree", "Disagree", "Neither Agree nor Disagree", "Agree", "Strongly Agree"];

const ReadOnlyTextField = withStyles({
  root: {
    "& .MuiInputBase-root.Mui-disabled": {
      color: "rgba(0, 0, 0, 0.8)",
      webkitTextFillColor: "rgba(0, 0, 0, 0.8)"
    }
  }
})(TextField);

const propTypes = {
  inputType: PropTypes.string.isRequired,
  readOnly: PropTypes.bool.isRequired,
  answer: PropTypes.string,
  onAnswerChange: PropTypes.func
};

const FeedbackAnswerInput = ({ inputType, readOnly, answer, onAnswerChange }) => {
  let inputField;

  const handleChange = useCallback((event, value) => {
    if (!onAnswerChange) return;
    if (inputType === "SLIDER") {
      onAnswerChange(agreeMarks[value]);
    } else {
      onAnswerChange(event.target.value);
    }
  }, [onAnswerChange, inputType]);

  switch (inputType) {
    case "TEXT":
      inputField = readOnly
        ? (
          <ReadOnlyTextField
            className="fullWidth feedback-answer-text-field"
            multiline
            disabled
            variant="outlined"
            InputProps={{
              readOnly: true
            }}
            value={answer}
          />
        )
        : (
          <TextField
            className="fullWidth feedback-answer-text-field"
            multiline
            rows={5}
            variant="outlined"
            onChange={handleChange}
            value={answer}
            onBlur={handleChange}
          />
        );
      break;
    case "RADIO":
      inputField = (
        <RadioGroup
          className="feedback-answer-radio"
          row
          value={answer}
          onChange={handleChange}
        >
          <FormControlLabel disabled={readOnly} value="Yes" control={<Radio/>} label="Yes"/>
          <FormControlLabel disabled={readOnly} value="No" control={<Radio/>} label="No"/>
          <FormControlLabel disabled={readOnly} value="I don't know" control={<Radio/>} label="I don't know"/>
        </RadioGroup>
      );
      break;
    case "SLIDER":
      inputField = (
        <Slider
          className="feedback-answer-slider"
          disabled={readOnly}
          min={0}
          max={agreeMarks.length - 1}
          value={agreeMarks.findIndex(mark => mark === answer)}
          step={1}
          marks={agreeMarks.map((mark, index) => {
            return { value: index, label: mark }
          })}
          onChange={handleChange}
        />
      );
      break;
    default:
      inputField = <></>;
      console.warn(`No input rendered for invalid inputType '${inputType}'`);
  }
  return inputField;
}

FeedbackAnswerInput.propTypes = propTypes;

export default FeedbackAnswerInput;