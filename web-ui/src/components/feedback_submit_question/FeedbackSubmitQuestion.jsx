import React from "react";
import PropTypes from "prop-types";

import Typography from "@mui/material/Typography";
import TextField from "@mui/material/TextField";

import "./FeedbackSubmitQuestion.css"
import RadioGroup from "@mui/material/RadioGroup";
import {FormControlLabel} from "@mui/material";
import Radio from "@mui/material/Radio";
import Slider from "@mui/material/Slider";

const agreeMarks = ["Strongly Disagree", "Disagree", "Neither Agree nor Disagree", "Agree", "Strongly Agree"];

const propTypes = {
  question: PropTypes.string.isRequired,
  questionNumber: PropTypes.number.isRequired,
  inputType: PropTypes.oneOf(["TEXT", "RADIO", "SLIDER"]).isRequired,
  readOnly: PropTypes.bool.isRequired,
  answer: PropTypes.string,
  handleAnswerChange: PropTypes.func
}

const FeedbackSubmitQuestion = (props) => {

  const getInput = () => {
    let inputField;
    switch (props.inputType) {
      case "TEXT":
        inputField = (
          <TextField
            multiline
            rows={5}
            className="fullWidth"
            variant="outlined"
            InputProps={{
              readOnly: props.readOnly
            }}
            onChange={(event) => {
              props.handleAnswerChange(event.target.value)
            }}
            value={props.answer}
          />
        );
        break;
      case "RADIO":
        inputField = (
          <RadioGroup row value={props.answer} onChange={(event) => {
            props.handleAnswerChange(event.target.value)
          }}>
            <FormControlLabel disabled={props.readOnly} value="Yes" control={<Radio/>} label="Yes"/>
            <FormControlLabel disabled={props.readOnly} value="No" control={<Radio/>} label="No"/>
            <FormControlLabel disabled={props.readOnly} value="I don't know" control={<Radio/>} label="I don't know"/>
          </RadioGroup>
        );
        break;
      case "SLIDER":
        inputField = (
          <Slider
            disabled={props.readOnly}
            min={0}
            max={agreeMarks.length - 1}
            value={agreeMarks.findIndex(mark => mark === props.answer)}
            step={1}
            marks={agreeMarks.map((mark, index) => {
              return { value: index, label: mark }
            })}
            onChange={(_, value) => {
              props.handleAnswerChange(agreeMarks[value])
            }}
          />
        );
        break;
      default:
        inputField = <></>;
        console.warn(`No input rendered for invalid inputType '${props.inputType}'`);
    }
    return inputField;
  }

  return (
    <div className="feedback-submit-question">
      <Typography variant="body1"><b>Q{props.questionNumber}:</b> {props.question}</Typography>
      {getInput()}
    </div>
  );
}

FeedbackSubmitQuestion.propTypes = propTypes;

export default FeedbackSubmitQuestion;