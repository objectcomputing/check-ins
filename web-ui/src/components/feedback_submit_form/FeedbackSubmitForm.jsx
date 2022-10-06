import React, {useCallback, useContext, useEffect, useState} from "react";
import {styled} from "@mui/material/styles";
import Typography from "@mui/material/Typography";
import PropTypes from "prop-types";
import {blue, green} from "@mui/material/colors";
import Button from "@mui/material/Button";
import "./FeedbackSubmitForm.css";
import {Alert, AlertTitle, Avatar, Chip} from "@mui/material";
import InfoIcon from "@mui/icons-material/Info";
import {Link, useHistory} from "react-router-dom";
import {AppContext} from "../../context/AppContext";
import {selectCsrfToken, selectProfile} from "../../context/selectors";
import {UPDATE_TOAST} from "../../context/actions";
import {updateAllAnswers, updateFeedbackRequest} from "../../api/feedback";
import {getQuestionAndAnswer} from "../../api/feedbackanswer"
import DateFnsUtils from "@date-io/date-fns";
import SkeletonLoader from "../skeleton_loader/SkeletonLoader";
import FeedbackSubmitQuestion from "../feedback_submit_question/FeedbackSubmitQuestion";
import {FeedbackRequestStatus} from "../../context/util";
import {getAvatarURL} from "../../api/api";

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
    '@media (max-width: 800px)': {
      fontSize: "22px"
    }
  },
  [`& .${classes.tip}`]: {
    '@media (max-width: 800px)': {
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
};

const FeedbackSubmitForm = ({ requesteeName, requestId, request }) => {
  const { state, dispatch } = useContext(AppContext);
  const csrf = selectCsrfToken(state);
  const requestCreator = selectProfile(state, request?.creatorId);
  const [isLoading, setIsLoading] = useState(false);
  const [isReviewing, setIsReviewing] = useState(false);
  const history = useHistory();
  const [questionAnswerPairs, setQuestionAnswerPairs] = useState([]);

  const handleAnswerChange = useCallback((index, newAnswer) => {
    // Update local state with answer data until assigned an ID
    let updatedQuestionAnswerPairs = [...questionAnswerPairs];

    updatedQuestionAnswerPairs[index].answer = {
      ...questionAnswerPairs[index].answer,
      ...newAnswer
    };
    setQuestionAnswerPairs(updatedQuestionAnswerPairs);

  }, [questionAnswerPairs]);

  async function updateRequestSubmit() {
    request.status = FeedbackRequestStatus.SUBMITTED;
    request.submitDate = dateUtils.format(new Date(), "yyyy-MM-dd")
    return await updateFeedbackRequest(request, csrf);
  }

  async function updateAllAnswersSubmit(){
    let answers = [];
    for (let i = 0; i < questionAnswerPairs.length; ++i) {
      answers.push(questionAnswerPairs[i].answer || {})
    }
    return await updateAllAnswers(answers, csrf)
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
      return await getQuestionAndAnswer(requestId, cookie)
    }

    if (csrf) {
      setIsLoading(true);
      getAllQuestionsAndAnswers(requestId, csrf).then((res) =>{
        if (res && res.payload && res.payload.data && !res.error) {
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
      <Typography className={classes.announcement} variant="h3">
        {request?.status === FeedbackRequestStatus.SUBMITTED ? "Viewing" : "Submitting"} Feedback on <b>{requesteeName}</b>
      </Typography>
      {request.status !== FeedbackRequestStatus.SUBMITTED &&
        <div className="wrapper">
          <InfoIcon style={{color: blue[900], fontSize: '24px'}}>info-icon</InfoIcon>
          <Typography className={classes.tip}><b>Tip of the day: </b>{tip}</Typography>
        </div>
      }
      {request.status === FeedbackRequestStatus.SUBMITTED
        ? (
          <Alert className={classes.warning} severity="success">
            <AlertTitle>
              <Typography fontWeight="bold">Submitted on {dateUtils.format(new Date(request.submitDate.join("/")), "MM/dd/yyyy")}</Typography>
            </AlertTitle>
            <Typography variant="body2">
              You have already submitted this feedback request. In order to change your answers, you must contact the creator of this feedback request to enable edits.
            </Typography>
            <div className="creator-details">
              <Typography variant="body2">This feedback request was sent to you by</Typography>
              <Chip
                avatar={<Avatar src={getAvatarURL(requestCreator?.workEmail)}/>}
                label={requestCreator?.name}
                clickable
                component={Link}
                to={`/profile/${requestCreator?.id}`}
              />
            </div>
          </Alert>
        )
        : isReviewing && (
          <Alert className={classes.warning} severity="warning">
            <AlertTitle>Notice!</AlertTitle>
            Feedback is not anonymous, and can be seen by more than just the feedback requester.
            <strong> Be mindful of your answers.</strong>
          </Alert>
        )
      }
      {questionAnswerPairs.map((questionAnswerPair, index) => (
        <FeedbackSubmitQuestion
          key={questionAnswerPair.question.id}
          question={questionAnswerPair.question}
          readOnly={request.status === FeedbackRequestStatus.SUBMITTED ? true : isReviewing}
          requestId={questionAnswerPair.request.id}
          answer={questionAnswerPair.answer}
          onAnswerChange={(newAnswer) => {
            handleAnswerChange(index, newAnswer);
          }}
        />
      ))}
      {request.status !== FeedbackRequestStatus.SUBMITTED &&
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
      }
    </Root>
  );
};

FeedbackSubmitForm.propTypes = propTypes;
export default FeedbackSubmitForm;
