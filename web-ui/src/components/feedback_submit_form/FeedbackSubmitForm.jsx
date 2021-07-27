import React, {useContext, useEffect, useState} from "react";
import Typography from "@material-ui/core/Typography";
import {makeStyles, withStyles} from '@material-ui/core/styles';
import PropTypes from "prop-types";
import {green} from '@material-ui/core/colors';
import FeedbackSubmitQuestion from "../feedback_submit_question/FeedbackSubmitQuestion";
import Button from "@material-ui/core/Button";
import "./FeedbackSubmitForm.css";
import {useHistory} from "react-router-dom";
import {Alert, AlertTitle} from "@material-ui/lab";

const useStyles = makeStyles({
  announcement: {
    textAlign: "center",
    ['@media (max-width: 800px)']: { // eslint-disable-line no-useless-computed-key
      fontSize: "22px"
    }
  },

  warning: {
    marginTop: "20px"
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
  requestId: PropTypes.string.isRequired,
}

const FeedbackSubmitForm = (props) => {
  const classes = useStyles();
  const history = useHistory();
  const handleClick = () => history.push(`/feedback/submit/confirmation/?request=${props.requestId}`);
  const [isReviewing, setIsReviewing] = useState(false);
  const location = useLocation();
  const {state, dispatch} = useContext(AppContext);
  const query = queryString.parse(location?.search);
  const idQuery = query.id?.toString();
  const currentUser = selectCurrentUser(state);


  const isIdValid = () => {
    if (idQuery !== null && idQuery !== undefined) {
      if (currentUser.id !== idQuery) {
        history.push("/checkins");
        dispatch({
          type: UPDATE_TOAST,
          payload: {
            severity: "error",
            toast: "You do not have Permission to Access this Request",
          },
        });
      }
    }
  }

    useEffect(() => {
      const getRequestInformation = async (id) => {
        let res = await getFeedbackRequestById(id, csrf);
        let data =
            res.payload && res.payload.data && !res.error
                ? res.payload.data
                : null;
        if (data) {
          console.log(":) " + JSON.stringify(data))
        }
      }
      if (csrf) {
        let requestId = '1c9a89d4-eafe-11eb-9a03-0242ac130003'
        getRequestInformation(requestId).then(r => console.log(r))
      }
      //   console.log("ID Query changed" + idQuery)
      //   if (currentUser.id !== idQuery) {
      //     history.push("/checkins");
      //     dispatch({
      //       type: UPDATE_TOAST,
      //       payload: {
      //         severity: "error",
      //         toast: "You do not have Permission to Access this Request",
      //       },
      //     });
      //   }
      // idRef.current = idQuery;
    }, [csrf]);

    return (
        <div className="submit-form">
          <Typography className={classes.announcement} variant="h3">Submitting Feedback on <b>{props.requesteeName}</b></Typography>
          {isReviewing ?
            <Alert className={classes.warning} severity="warning">
              <AlertTitle>Notice!</AlertTitle>
              Feedback is not anonymous, and can be seen by more than just the feedback requester.
              <strong> Be mindful of your answers, for you never know who will see it!</strong>
            </Alert> : null
          }
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
