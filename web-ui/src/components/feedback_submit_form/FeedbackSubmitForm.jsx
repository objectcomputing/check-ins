import React, { useContext, useEffect, useState } from "react";
import Typography from "@material-ui/core/Typography";
import { makeStyles, withStyles } from '@material-ui/core/styles';
import PropTypes from "prop-types";
import { green } from '@material-ui/core/colors';
import FeedbackSubmitQuestion from "../feedback_submit_question/FeedbackSubmitQuestion";
import Button from "@material-ui/core/Button";
import "./FeedbackSubmitForm.css";
import { Alert, AlertTitle } from "@material-ui/lab";
import InfoIcon from '@material-ui/icons/Info';
import { blue } from "@material-ui/core/colors";
import { useHistory } from "react-router-dom";
import { AppContext } from "../../context/AppContext";
import { selectCsrfToken } from "../../context/selectors";
import {
  getAllAnswersFromRequestAndQuestionId,
  saveAllAnswers,
  getQuestionsByRequestId,
  getAnswerByRequestAndQuestionId,
  updateSingleAnswer
} from "../../api/feedback";
import TextField from "@material-ui/core/TextField";


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

const tip = randomTip[Math.floor(Math.random() * randomTip.length)];

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

const FeedbackSubmitForm = ({ requesteeName, requestId }) => {
  const { state } = useContext(AppContext);
  const csrf = selectCsrfToken(state);
  const classes = useStyles();
  const handleClick = () => history.push(`/feedback/submit/confirmation/?request=${requestId}`);
  const [isReviewing, setIsReviewing] = useState(false);
  const history = useHistory();
  const [questionAnswerPairs, setQuestionAnswerPairs] = useState([])

  const updateAnswer = async (answer) => {
    if (csrf) {
    const res = await updateSingleAnswer( answer, csrf)
    console.log("res in update answer " + JSON.stringify(res))
    return res;
    }
  }

  const onChangeHandler= (event, index) => {
    console.log("on change handler fired " + event.target.value + " " + index)
    let questionAnswerCopy = questionAnswerPairs;
    questionAnswerCopy[index].answer.answer = event.target.value;
    console.log(questionAnswerCopy)
    updateAnswer(questionAnswerCopy[index].answer);
    setQuestionAnswerPairs(questionAnswerCopy);
  }

  useEffect(() => {
    console.log("question answer apirs : " + JSON.stringify(questionAnswerPairs))

  }, [questionAnswerPairs])



  useEffect(() => {
    async function getQuestions(requestId, cookie) {
      if (!requestId) return;
      const res = await getQuestionsByRequestId(requestId, cookie);
      let questionsList = res.questions ? res.questions : [];
      return questionsList;
    }

    async function getAnswers(questionsList) {
      if (!questionsList || questionsList === undefined) {
        return;
      }
      const res = await getAllAnswersFromRequestAndQuestionId(requestId, questionsList, csrf)
      return res;
    }

    async function saveAnswersList(questionList, cookie) {
      let answers = []
      for (let i = 0; i < questionList.length; ++i) {
        let newAnswer = { answer: "", questionId: questionList[i].id, requestId: requestId, sentiment: null }
        answers.push(newAnswer)
      }
      const res = await saveAllAnswers(answers, cookie)
      console.log(res);
    }

    if (csrf) {
      getQuestions(requestId, csrf).then((questionsList) => {
        getAnswers(questionsList).then((answers) => {
          console.log("Answers " + JSON.stringify(answers))
          setQuestionAnswerPairs(answers)
        })
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
          Feedback is not anonymous, and can be seen by more than just the feedback requester.
          <strong> Be mindful of your answers.</strong>
        </Alert> : null
      }
      {questionAnswerPairs.map((questionAnswerPair, index) => (
        <div className="feedback-submit-question" key={questionAnswerPair.question.id}>
          <Typography variant="body1"><b>Q{questionAnswerPair.question.questionNumber}:</b> {questionAnswerPair.question.question}</Typography>
          <TextField
            className="fullWidth"
            variant="outlined"
            multiline
            rowsMax={20}
            onChange={(e)=> onChangeHandler(e, index)}
          />
        </div>

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