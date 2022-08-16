import React, {useCallback, useContext, useEffect, useRef} from "react";
import PropTypes from "prop-types";

import Typography from "@mui/material/Typography";
import TextField from "@mui/material/TextField";

import "./FeedbackSubmitQuestion.css"
import RadioGroup from "@mui/material/RadioGroup";
import {FormControlLabel} from "@mui/material";
import Radio from "@mui/material/Radio";
import Slider from "@mui/material/Slider";
import {debounce} from "lodash/function";
import {saveSingleAnswer, updateSingleAnswer} from "../../api/feedback";
import {AppContext} from "../../context/AppContext";
import {UPDATE_TOAST} from "../../context/actions";

const agreeMarks = ["Strongly Disagree", "Disagree", "Neither Agree nor Disagree", "Agree", "Strongly Agree"];

const propTypes = {
  question: PropTypes.shape({
    id: PropTypes.string.isRequired,
    question: PropTypes.string.isRequired,
    questionNumber: PropTypes.number.isRequired,
    inputType: PropTypes.oneOf(["TEXT", "RADIO", "SLIDER"]).isRequired
  }),
  readOnly: PropTypes.bool.isRequired,
  answer: PropTypes.shape({
    id: PropTypes.string,
    answer: PropTypes.string,
    questionId: PropTypes.string,
    requestId: PropTypes.string
  }),
  requestId: PropTypes.string.isRequired,
  onAnswerChange: PropTypes.func
};

const updateAnswer = async (answer, csrf) => {
  return await updateSingleAnswer(answer, csrf);
}

const updateAnswerWithDebounce = debounce(updateAnswer, 2000);

const FeedbackSubmitQuestion = (props) => {

  const { state, dispatch } = useContext(AppContext);
  const { csrf } = state;

  const savingAnswer = useRef(false);

  const saveAnswer = useCallback((answer) => {
    saveSingleAnswer(answer, csrf).then(res => {
      if (res?.payload?.data && !res.error) {
        const answerWithId = {
          ...answer,
          id: res.payload.data.id
        };
        props.onAnswerChange(answerWithId);
      } else {
        savingAnswer.current = false;
        dispatch({
          type: UPDATE_TOAST,
          payload: {
            severity: "error",
            toast: "Failed to save answer"
          }
        });
      }
    });
  }, [csrf, dispatch, props]);

  useEffect(() => {
    if (props.answer && props.answer.id) {
      savingAnswer.current = false;
    }
  }, [props.answer]);

  const handleAnswerChange = (answerText) => {
    if (props.answer && props.answer.id) {
      props.onAnswerChange({
        id: props.answer.id,
        answer: answerText
      });

      const updatedAnswer = {
        ...props.answer,
        answer: answerText
      };

      updateAnswerWithDebounce(updatedAnswer, csrf);
    } else if (!savingAnswer.current) {
      props.onAnswerChange({
        answer: answerText
      });

      const newAnswer = {
        answer: answerText,
        questionId: props.question.id,
        requestId: props.requestId
      };

      saveAnswer(newAnswer);
    }
  }

  const getInput = () => {
    let inputField;
    switch (props.question.inputType) {
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
              handleAnswerChange(event.target.value)
            }}
            value={props.answer?.answer}
          />
        );
        break;
      case "RADIO":
        inputField = (
          <RadioGroup row value={props.answer?.answer} onChange={(event) => {
            handleAnswerChange(event.target.value)
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
            value={agreeMarks.findIndex(mark => mark === props.answer?.answer)}
            step={1}
            marks={agreeMarks.map((mark, index) => {
              return { value: index, label: mark }
            })}
            onChange={(_, value) => {
              handleAnswerChange(agreeMarks[value])
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
      <Typography variant="body1"><b>Q{props.question.questionNumber}:</b> {props.question.question}</Typography>
      {getInput()}
    </div>
  );
}

FeedbackSubmitQuestion.propTypes = propTypes;

export default FeedbackSubmitQuestion;