import React from "react";
import PropTypes from "prop-types";

import Typography from "@material-ui/core/Typography";
import TextField from "@material-ui/core/TextField";

import "./FeedbackSubmitQuestion.css"

const propTypes = {
  question: PropTypes.string.isRequired,
  questionNumber: PropTypes.number.isRequired,
  editable: PropTypes.bool.isRequired
}

const FeedbackSubmitQuestion = (props) => {
  return (
    <div className="feedback-submit-question">
      <Typography variant="body1"><b>Q{props.questionNumber}:</b> {props.question}</Typography>
      <TextField
        className="fullWidth"
        variant="outlined"
        placeholder="Type your answer..."
        multiline
        rowsMax={20}
        InputProps={{
          readOnly: !props.editable
        }}
      />
    </div>
  );
}

FeedbackSubmitQuestion.propTypes = propTypes;

export default FeedbackSubmitQuestion;