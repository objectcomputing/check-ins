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

const AnswerInput = ({ inputType, readOnly, answer, onAnswerChange }) => {
  let inputField;

  const handleChange = useCallback((event, value) => {
    if (inputType === "SLIDER") {
      onAnswerChange(agreeMarks[value]);
    } else {
      onAnswerChange(event.target.value);
    }

  }, [onAnswerChange, inputType]);

  switch (inputType) {
    case "TEXT":
      inputField = (
        <TextField
          multiline
          rows={5}
          className="fullWidth"
          variant="outlined"
          InputProps={{
            readOnly: readOnly
          }}
          onChange={handleChange}
          value={answer}
          onBlur={handleChange}
        />
      );
      break;
    case "RADIO":
      inputField = (
        <RadioGroup
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

const FeedbackSubmitQuestion = (props) => {

  const { state, dispatch } = useContext(AppContext);
  const { csrf } = state;

  const savingAnswer = useRef(false);
  const updateAnswerWithDebounce = useRef(debounce(updateSingleAnswer, 2000));

  useEffect(() => {
    if (props.answer?.id) {
      savingAnswer.current = false;
    }
  }, [props.answer?.id]);

  const saveAnswer = useCallback((answer) => {
    savingAnswer.current = true;
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

  const saveAnswerWithDebounce = useRef(debounce(saveAnswer, 2000));

  const handleSaveAnswer = useCallback((answerText) => {
    props.onAnswerChange({
      answer: answerText
    });

    if (!savingAnswer.current) {
      const newAnswer = {
        answer: answerText,
        questionId: props.question.id,
        requestId: props.requestId
      };

      const save = saveAnswerWithDebounce.current;
      save(newAnswer);
    }
  }, [props, savingAnswer]);

  const handleUpdateAnswer = useCallback((answerText) => {
    if (props.answer && props.answer.id) {
      props.onAnswerChange({
        id: props.answer.id,
        answer: answerText
      });

      const updatedAnswer = {
        ...props.answer,
        answer: answerText
      };

      const update = updateAnswerWithDebounce.current;
      update(updatedAnswer, csrf);
    }
  }, [csrf, props]);

  return (
    <div className="feedback-submit-question">
      <Typography variant="body1"><b>Q{props.question.questionNumber}:</b> {props.question.question}</Typography>
      <AnswerInput
        answer={props.answer?.answer}
        readOnly={props.readOnly}
        inputType={props.question?.inputType}
        onAnswerChange={props.answer?.id ? handleUpdateAnswer : handleSaveAnswer}
      />
    </div>
  );
}

FeedbackSubmitQuestion.propTypes = propTypes;

export default FeedbackSubmitQuestion;