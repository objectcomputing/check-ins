import React, { useContext, useEffect, useState } from "react";
import Typography from "@material-ui/core/Typography";
import { makeStyles, withStyles } from '@material-ui/core/styles';
import PropTypes from "prop-types";
import { green } from '@material-ui/core/colors';
import Button from "@material-ui/core/Button";
import Slider from '@material-ui/core/Slider';
import Radio from '@material-ui/core/Radio';
import RadioGroup from '@material-ui/core/RadioGroup';
import FormControlLabel from '@material-ui/core/FormControlLabel';
import "./FeedbackSubmitForm.css";
import { Alert, AlertTitle } from "@material-ui/lab";
import InfoIcon from '@material-ui/icons/Info';
import { blue } from "@material-ui/core/colors";
import { useHistory } from "react-router-dom";
import { AppContext } from "../../context/AppContext";
import { selectCsrfToken } from "../../context/selectors";
import { UPDATE_TOAST } from "../../context/actions";
import {
  getAllAnswersFromRequestAndQuestionId,
  updateAllAnswers,
  getQuestionsByRequestId,
  updateSingleAnswer,
  updateFeedbackRequest
} from "../../api/feedback";
import TextField from "@material-ui/core/TextField";
import { debounce } from "lodash/function";
import DateFnsUtils from "@date-io/date-fns";

const dateUtils = new DateFnsUtils();

const frequencyMarks = [
  {
    value: 0,
    label: "Very Infrequently",
    text: "Very Infrequently"
  },
  {
    value: 1,
    text: "Infrequently"
  },
  {
    value: 2,
    text: "Neither Frequently nor Infrequently"
  },
  {
    value: 3,
    text: "Frequently"
  },
  {
    value: 4,
    label: "Very Frequently",
    text: "Very Frequently"
  }
];

const agreeMarks = [
  {
    value: 0,
    label: "Strongly Disagree",
    text: "Strongly Disagree"
  },
  {
    value: 1,
    text: "Disagree"
  },
  {
    value: 2,
    text: "Neither Agree nor Disagree"
  },
  {
    value: 3,
    text: "Agree"
  },
  {
    value: 4,
    label: "Strongly Agree",
    text: "Strongly Agree"
  }
];

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
  request: PropTypes.any.isRequired,
}

const getSliderValue = (marks, text) => {
  const value = marks?.find((mark) => mark?.text === text)?.value
  return value;
}

const FeedbackSubmitForm = ({ requesteeName, requestId, request }) => {
  const { state, dispatch } = useContext(AppContext);
  const csrf = selectCsrfToken(state);
  const classes = useStyles();
  const [isReviewing, setIsReviewing] = useState(false);
  const history = useHistory();
  const [questionAnswerPairs, setQuestionAnswerPairs] = useState([])
  const [templateTitle, setTemplateTitle] = useState(null)
  let currentlyBeingEdited = -1

  const updateAnswer = debounce(async () => {
    if (csrf) {
      const res = updateSingleAnswer(questionAnswerPairs[currentlyBeingEdited].answer, csrf)
      return res;
    }
  }, 1000)

  async function updateRequestSubmit() {
    request.status = "submitted"
    request.submitDate = dateUtils.format(new Date(), "yyyy-MM-dd")
    const res = await updateFeedbackRequest(request, csrf);
    return res;
  }

  async function updateAllAnswersSubmit(){
    let answers= [];
    for (let i = 0; i < questionAnswerPairs.length; ++i) {
      answers.push(questionAnswerPairs[i].answer)
    }
    const res = await updateAllAnswers(answers, csrf)
    return res;
  }

  const onSubmitHandler =() => {
    updateAllAnswersSubmit().then((res) => {
      for (let i = 0; i < res.length; ++i ) {
        if (res[i].error) {
          dispatch({
            type: UPDATE_TOAST,
            payload: {
              severity: "error",
              toast: res[i].error,
            },
          });
          return false;
        }
      }
      return true;
    }).then((resTwo) => {
      if (resTwo === false) {
        return;
      }
      updateRequestSubmit().then((res) => {
        if (res && res.payload && res.payload.data && !res.error) {
         history.push(`/feedback/submit/confirmation/?request=${requestId}`)
        } else {
          dispatch({
            type: UPDATE_TOAST,
            payload: {
              severity: "error",
              toast: res.error,
            },
          });
        }
      })

    })
  }

  const onSliderChange = (event, index, marks, value) => {
    let questionAnswerCopy = [...questionAnswerPairs];
    questionAnswerCopy[index].answer.answer = marks?.find((mark) => mark.value === value)?.text;
    currentlyBeingEdited = index;
    setQuestionAnswerPairs(questionAnswerCopy);
    updateAnswer();
  }

  const onRadioChange = (event, index, value) => {
    let questionAnswerCopy = [...questionAnswerPairs];
    questionAnswerCopy[index].answer.answer = value;
    currentlyBeingEdited = index;
    setQuestionAnswerPairs(questionAnswerCopy);
    updateAnswer()
  }

  const onChangeHandler = (event, index) => {
    let questionAnswerCopy = [...questionAnswerPairs];
    questionAnswerCopy[index].answer.answer = event.target.value;
    currentlyBeingEdited = index;
    setQuestionAnswerPairs(questionAnswerCopy);
    updateAnswer();
  }

  const getQuestionHeader = (index, isReview) => isReview && index === 1
    && (<h2>How often has this team member displayed each of the following in the past year...</h2>);

  const getReviewInput = (questionAnswerPair, isReviewing, index) => {
    let toReturn = null;
    switch(index) {
      case 0:
      case 8:
        // Strongly Disagree - Strongly Agree
        toReturn =
          (<Slider
            disabled={isReviewing}
            min={0}
            max={4}
            value={getSliderValue(agreeMarks, questionAnswerPair.answer.answer)}
            step={1}
            marks={agreeMarks}
            onChange={(e, value) => onSliderChange(e, index, agreeMarks, value)}
          />);
        break;
      case 7:
      case 12:
        toReturn =
          (<TextField
            multiline
            rows={5}
            rowsMax={10}
            className="fullWidth"
            variant="outlined"
            InputProps={{
              readOnly: isReviewing,
            }}
            onChange={(e) => onChangeHandler(e, index)}
            defaultValue={questionAnswerPair.answer.answer}
          />);
        break;
      case 9:
      case 10:
      case 11:
        // Yes, No, I don't know...
        toReturn =
          (<RadioGroup row value={questionAnswerPair.answer.answer} onChange={(event, value) => onRadioChange(event, index, value)}>
            <FormControlLabel disabled={isReviewing} value="Yes" control={<Radio />} label="Yes" />
            <FormControlLabel disabled={isReviewing} value="No" control={<Radio />} label="No" />
            <FormControlLabel disabled={isReviewing} value="I don't know." control={<Radio />} label="I don't know" />
          </RadioGroup>);
        break;
      default:
        // Very Infrequently - Very Frequently
        toReturn =
          (<Slider
            disabled={isReviewing}
            min={0}
            max={4}
            value={getSliderValue(frequencyMarks, questionAnswerPair.answer.answer)}
            step={1}
            marks={frequencyMarks}
            onChange={(e, value) => onSliderChange(e, index, frequencyMarks, value)}
          />);
        break;
    }

    return toReturn;
  }

  const getInput = (questionAnswerPair, isReviewing, index, isReview) => {
      return !isReview ? (<TextField
          className="fullWidth"
          variant="outlined"
          multiline
          rows={10}
          rowsMax={20}
          InputProps={{
            readOnly: isReviewing,
          }}
          onChange={(e) => onChangeHandler(e, index)}
          defaultValue={questionAnswerPair.answer.answer}
        />) : getReviewInput(questionAnswerPair, isReviewing, index);
  }

  useEffect(() => {
    async function getQuestions(requestId, cookie) {
      if (!requestId) return;
      const res = await getQuestionsByRequestId(requestId, cookie);
      setTemplateTitle(res?.title);
      let questionsList = res?.questions ? res.questions : [];
      return questionsList;
    }

    async function getAnswers(questionsList) {
      if (!questionsList) {
        return;
      }
      const res = getAllAnswersFromRequestAndQuestionId(requestId, questionsList, csrf)
      return res;
    }

    if (csrf) {
      getQuestions(requestId, csrf).then((questionsList) => {
        getAnswers(questionsList).then((answers) => {
          setQuestionAnswerPairs(answers)
        })
      });
    }
  }, [requestId, csrf]);

  const isReview = templateTitle === "Annual Review";

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
          {getQuestionHeader(index, isReview)}
          <Typography variant="body1"><b>Q{questionAnswerPair.question.questionNumber}:</b> {questionAnswerPair.question.question}</Typography>
          {getInput(questionAnswerPair, isReviewing, index, isReview)}
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
              onClick={onSubmitHandler}
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
