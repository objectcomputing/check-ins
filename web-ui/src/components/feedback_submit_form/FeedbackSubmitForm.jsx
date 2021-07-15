import React, {useState} from "react";
import Typography from "@material-ui/core/Typography";
import {makeStyles, withStyles} from '@material-ui/core/styles';
import PropTypes from "prop-types";
import {green} from '@material-ui/core/colors';

import FeedbackSubmitQuestion from "../feedback_submit_question/FeedbackSubmitQuestion";
import Button from "@material-ui/core/Button";

import "./FeedbackSubmitForm.css";
import {useHistory} from "react-router-dom";

const useStyles = makeStyles({
  announcement: {
    textAlign: "center",
    ['@media (max-width: 800px)']: { // eslint-disable-line no-useless-computed-key
      fontSize: "22px"
    }
  },
  button: {
    margin: "3em 1em 1em 1em"
  }
});

const sampleQuestions = [
  {
    id: 1,
    question: "How are you doing today?"
  },
  {
    id: 2,
    question: "How is the project going?"
  },
  {
    id: 3,
    question: "What is your current role on the team?"
  }
];

const ColorButton = withStyles({
  root: {
    color: "white",
    backgroundColor: green[500],
    '&:hover': {
      backgroundColor: green[700],
    },
  },
})(Button);

const propTypes = {
  requesteeName: PropTypes.string.isRequired,
}

const FeedbackSubmitForm = (props) => {
  const classes = useStyles();
  const history = useHistory();
  const handleClick = () => history.push('/feedback/submit/confirmation');

  const [isReviewing, setIsReviewing] = useState(false);

  return (
    <div className="submit-form">
      <Typography className={classes.announcement} variant="h3">Submitting Feedback on <b>{props.requesteeName}</b></Typography>
      {sampleQuestions.map((sampleQuestion) => (
        <FeedbackSubmitQuestion
          key={sampleQuestion.id}
          question={sampleQuestion.question}
          questionNumber={sampleQuestion.id}
          editable={!isReviewing}
        />
      ))}
      <div className="submit-action-buttons">
        {isReviewing ?
          <React.Fragment>
            <ColorButton
              className={classes.button}
              onClick={() => setIsReviewing(false)}
              variant="contained"
              color="primary">
              Edit
            </ColorButton>
            <Button
              className={classes.button}
              onClick={handleClick}
              variant="contained"
              color="primary">
              Submit
            </Button>
          </React.Fragment> :
          <ColorButton
            className={classes.button}
            onClick={() => setIsReviewing(true)}
            variant="contained"
            color="primary">
            Review
          </ColorButton>
        }
      </div>
    </div>
  );
};

FeedbackSubmitForm.propTypes = propTypes;

export default FeedbackSubmitForm;
