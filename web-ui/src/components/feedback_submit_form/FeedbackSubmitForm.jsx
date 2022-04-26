import React, { useContext, useEffect, useState, useCallback } from "react";
import { styled } from "@mui/material/styles";
import Typography from "@mui/material/Typography";
import PropTypes from "prop-types";
import { green } from "@mui/material/colors";
import Button from "@mui/material/Button";
import "./FeedbackSubmitForm.css";
import { Alert, AlertTitle } from "@mui/material";
import InfoIcon from "@mui/icons-material/Info";
import { blue } from "@mui/material/colors";
import { useHistory } from "react-router-dom";
import { AppContext } from "../../context/AppContext";
import { selectCsrfToken } from "../../context/selectors";
import { UPDATE_TOAST } from "../../context/actions";
import {
  updateAllAnswers,
  updateSingleAnswer,
  updateFeedbackRequest, saveSingleAnswer,
} from "../../api/feedback";
import {
getQuestionAndAnswer
} from "../../api/feedbackanswer"
import { debounce } from "lodash/function";
import DateFnsUtils from "@date-io/date-fns";
import SkeletonLoader from "../skeleton_loader/SkeletonLoader";
import FeedbackSubmitQuestion from "../feedback_submit_question/FeedbackSubmitQuestion";

const dateUtils = new DateFnsUtils();
const PREFIX = "FeedbackSubmitForm";
const classes = {
  announcement: `${PREFIX}-announcement`,
  tip: `${PREFIX}-tip`,
  warning: `${PREFIX}-warning`,
  button: `${PREFIX}-button`,
  coloredButton: `${PREFIX}-coloredButton`
};

const Root = styled('div')({
  [`& .${classes.announcement}`]: {
    textAlign: "center",
    ['@media (max-width: 800px)']: { // eslint-disable-line no-useless-computed-key
      fontSize: "22px"
    }
  },
  [`& .${classes.tip}`]: {
    ['@media (max-width: 800px)']: { // eslint-disable-line no-useless-computed-key
      fontSize: "15px"
    }
  },
  [`& .${classes.warning}`]: {
    marginTop: "20px"
  },
  [`& .${classes.button}`]: {
    margin: "3em 1em 1em 1em",
  },
  [`& .${classes.coloredButton}`]: {
    margin: "3em 1em 1em 1em",
    color: "white",
    backgroundColor: green[500],
    '&:hover': {
      backgroundColor: green[700],
    },
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

const propTypes = {
  requesteeName: PropTypes.string.isRequired,
  requestId: PropTypes.string.isRequired,
  request: PropTypes.any.isRequired,
}

const FeedbackSubmitForm = ({ requesteeName, requestId, request }) => {
  const { state, dispatch } = useContext(AppContext);
  const csrf = selectCsrfToken(state);
  const [isLoading, setIsLoading] = useState(false);
  const [isReviewing, setIsReviewing] = useState(false);
  const history = useHistory();
  const [questionAnswerPairs, setQuestionAnswerPairs] = useState([]);
  const [templateTitle, setTemplateTitle] = useState(null);

  const updateFeedbackAnswer = async (index, answer) => {
    let res;

    // Save or update answer on server
    if (answer && csrf) {
      if (answer.id) {
        res = await updateSingleAnswer(answer, csrf);
      } else {
        res = await saveSingleAnswer(answer, csrf);
      }
    }

    // Update local state with new answer ID
    if (res && res.payload && res.payload.data && !res.error) {
      let updatedQuestionAnswerPairs = [...questionAnswerPairs];
      updatedQuestionAnswerPairs[index].answer.id = res.payload.data.id;
      setQuestionAnswerPairs(updatedQuestionAnswerPairs);
    }
  }

  const updateFeedbackAnswerWithDebounce = debounce(updateFeedbackAnswer, 1000);

  const handleAnswerChange = useCallback((index, newAnswer) => {
    // Update local state with answer data until assigned an ID
    let updatedQuestionAnswerPairs = [...questionAnswerPairs];
    updatedQuestionAnswerPairs[index].answer = {...newAnswer};
    setQuestionAnswerPairs(updatedQuestionAnswerPairs);

    // Save or update new answer with debounce
    updateFeedbackAnswerWithDebounce(index, newAnswer);
  }, [questionAnswerPairs, updateFeedbackAnswerWithDebounce]);

  async function updateRequestSubmit() {
    request.status = "submitted"
    request.submitDate = dateUtils.format(new Date(), "yyyy-MM-dd")
    const res = await updateFeedbackRequest(request, csrf);
    return res;
  }

  async function updateAllAnswersSubmit(){
    let answers = [];
    for (let i = 0; i < questionAnswerPairs.length; ++i) {
      answers.push(questionAnswerPairs[i].answer || {})
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
      });
    });
  }

  useEffect(() => {
    async function getAllQuestionsAndAnswers(requestId, cookie) {
      if (!requestId) {
        return;
      }
      const res = await getQuestionAndAnswer(requestId, cookie)
      return res;
    }

    if (csrf) {
      setIsLoading(true);
      getAllQuestionsAndAnswers(requestId, csrf).then((res) =>{
        if (res && res.payload && res.payload.data && !res.error) {
          setTemplateTitle(res?.title);
          setQuestionAnswerPairs(res.payload.data)
        } else {
          dispatch({
            type: UPDATE_TOAST,
            payload: {
              severity: "error",
              toast: res.error,
            },
          });
        }
        setIsLoading(false);
      });
    }
  }, [requestId, csrf, dispatch]);

  return isLoading ? <SkeletonLoader type="feedback_requests" /> : (
    <Root className="submit-form">
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
        <FeedbackSubmitQuestion
          key={questionAnswerPair.question.id}
          question={questionAnswerPair.question.question}
          questionNumber={questionAnswerPair.question.questionNumber}
          inputType={questionAnswerPair.question.inputType}
          readOnly={isReviewing}
          answer={questionAnswerPair.answer?.answer}
          handleAnswerChange={(newAnswer) => {
            handleAnswerChange(index, {
              answer: newAnswer,
              id: questionAnswerPair.answer?.id,
              questionId: questionAnswerPair.question.id,
              requestId: questionAnswerPair.request.id
            });
          }}
        />
      ))}
      <div className="submit-action-buttons">
        {isReviewing ?
        (<React.Fragment>
            <Button
              className={classes.coloredButton}
              disabled={isLoading}
              onClick={() => setIsReviewing(false)}
              variant="contained"
              color="primary">
              Edit
            </Button>
            <Button
              className={classes.button}
              disabled={isLoading}
              onClick={onSubmitHandler}
              variant="contained"
              color="primary">
              Submit
            </Button>
          </React.Fragment>) :
          <Button
            className={classes.coloredButton}
            disabled={isLoading}
            onClick={() => setIsReviewing(true)}
            variant="contained"
            color="primary">
            Review
          </Button>}
      </div>
    </Root>
  );
};

FeedbackSubmitForm.propTypes = propTypes;
export default FeedbackSubmitForm;
