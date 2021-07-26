import React, {useContext, useEffect, useState} from "react";
import Typography from "@material-ui/core/Typography";
import {makeStyles, withStyles} from '@material-ui/core/styles';
import PropTypes from "prop-types";
import {green} from '@material-ui/core/colors';
import FeedbackSubmitQuestion from "../feedback_submit_question/FeedbackSubmitQuestion";
import Button from "@material-ui/core/Button";
import "./FeedbackSubmitForm.css";
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
  button: {
    margin: "3em 1em 1em 1em"
  }
});

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
  const history = useHistory();
  const handleClick = () => history.push(`/feedback/submit/confirmation/?request=${requestId}`);
  const [isReviewing, setIsReviewing] = useState(false);
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
