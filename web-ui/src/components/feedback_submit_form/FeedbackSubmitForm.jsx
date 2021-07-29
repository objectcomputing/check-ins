import React, {useContext, useEffect, useState} from "react";
import Typography from "@material-ui/core/Typography";
import {makeStyles, withStyles} from '@material-ui/core/styles';
import PropTypes from "prop-types";
import {green} from '@material-ui/core/colors';
import FeedbackSubmitQuestion from "../feedback_submit_question/FeedbackSubmitQuestion";
import Button from "@material-ui/core/Button";
import "./FeedbackSubmitForm.css";
import {Alert, AlertTitle} from "@material-ui/lab";
import InfoIcon from '@material-ui/icons/Info';
import { blue } from "@material-ui/core/colors";
import {useHistory} from "react-router-dom";
import {AppContext} from "../../context/AppContext";
import {selectCsrfToken} from "../../context/selectors";
import {getQuestionsByRequestId} from "../../api/feedback";


const useStyles = makeStyles({
  announcement: {
    textAlign: "center",
    ['@media (max-width: 800px)']: { // eslint-disable-line no-useless-computed-key
      fontSize: "22px"
    }
  },

  tip: {
    ['@media (max-width: 800px)']: { // eslint-disable-line no-useless-computed-key
      fontSize: "15px"
    }
  },

  warning: {
    marginTop: "20px"
  },

  button: {
    margin: "3em 1em 1em 1em"
  }
});

const randomTip = [
  'Take a Positive Approach.',
  'Focus on the Issue - Not the Person.',
  'Be Specific About What Needs to Change.',
  'Be specific.',
  'Explain the impact.',
  'Provide a summary.',
  'Recommend a solution.',
  'Be sincere.',
];

const tip = randomTip[Math.floor(Math.random()*randomTip.length)];

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
  requestId: PropTypes.string.isRequired,
}

const FeedbackSubmitForm = ({requesteeName, requestId}) => {
  const {state} = useContext(AppContext);
  const csrf = selectCsrfToken(state);
  const classes = useStyles();
  const handleClick = () => history.push(`/feedback/submit/confirmation/?request=${requestId}`);
  const [isReviewing, setIsReviewing] = useState(false);
  const history = useHistory();
  const [questions, setQuestions] = useState([]);

  useEffect(() => {
    async function getQuestions(requestId, cookie) {
      if (!requestId) return;
      const res = await getQuestionsByRequestId(requestId, cookie)
      let questionsList = res && res.payload && res.payload.data && !res.error ? res.payload.data : [];
      console.log(questionsList)
      return questionsList;
    }
    if (csrf) {
      let requestId = 'ab7b21d4-f88c-4494-9b0b-8541636025eb'
      getQuestions(requestId, csrf).then((questionsList) => {
        setQuestions(questionsList);
      });
    }
  }, [requestId, csrf]);

  return (
      <div className="submit-form">
        <Typography className={classes.announcement} variant="h3">Submitting Feedback on <b>{requesteeName}</b></Typography>
        <div className="wrapper">
          <InfoIcon style={{ color: blue[900], fontSize: '2vh' }}>info-icon</InfoIcon>
          <Typography className={classes.tip}><b>Tip of the day: </b>{tip}</Typography>
        </div>
        {isReviewing ?
          <Alert className={classes.warning} severity="warning">
            <AlertTitle>Notice!</AlertTitle>
            Feedback is not anonymous, and can be seen by more than just the feedback Requester.
            <strong> Be mindful of your answers.</strong>
          </Alert> : null
        }
        {questions.map((question) => (
            <FeedbackSubmitQuestion
                key={question.id}
                question={question.question}
                questionNumber={question.questionNumber}
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