import React, {useCallback, useContext, useEffect, useRef} from "react";
import PropTypes from "prop-types";

import Typography from "@mui/material/Typography";

import "./FeedbackSubmitQuestion.css"
import {debounce} from "lodash/function";
import {saveSingleAnswer, updateSingleAnswer} from "../../api/feedback";
import {AppContext} from "../../context/AppContext";
import {UPDATE_TOAST} from "../../context/actions";
import FeedbackAnswerInput from "../feedback_answer_input/FeedbackAnswerInput";

const propTypes = {
  question: PropTypes.shape({
    id: PropTypes.string.isRequired,
    question: PropTypes.string.isRequired,
    questionNumber: PropTypes.number.isRequired,
    inputType: PropTypes.oneOf(["TEXT", "RADIO", "SLIDER","NONE"]).isRequired
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

  const isStaticText = props?.question?.inputType?.toUpperCase() === "NONE";

  return (
    <div className="feedback-submit-question">
      <Typography style={isStaticText ? {paddingTop: "2rem"} : {paddingBottom: "1rem"}} variant={ isStaticText ? "h6" : "body1" }>{props.question.question}</Typography>
      <FeedbackAnswerInput
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