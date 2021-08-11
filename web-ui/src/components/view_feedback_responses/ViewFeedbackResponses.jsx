import React, {useContext, useEffect, useState} from 'react';
import {Typography} from '@material-ui/core';
import "./ViewFeedbackResponses.css";
import {makeStyles} from '@material-ui/core/styles';
import FeedbackResponseCard from "./feedback_response_card/FeedbackResponseCard";
import {getQuestionsAndAnswers} from "../../api/feedbackanswer";
import {getFeedbackRequestById} from "../../api/feedback"
import queryString from "query-string";
import {useLocation} from "react-router-dom";
import {AppContext} from "../../context/AppContext";
import {selectCsrfToken, selectProfile} from "../../context/selectors";
import {UPDATE_TOAST} from "../../context/actions";

const useStylesCardContent = makeStyles({
  root: {
    '&:last-child': {
      paddingBottom: '16px',
    }
  }
}, {name: "MuiCardContent"});

const ViewFeedbackResponses = () => {
  useStylesCardContent();
  const { state } = useContext(AppContext);
  const csrf = selectCsrfToken(state);
  const location = useLocation();
  const [query, setQuery] = useState({});
  const [questionsAndAnswers, setQuestionsAndAnswers] = useState([]);
  const [requestInfo, setRequestInfo] = useState({});

  useEffect(() => {
    setQuery(queryString.parse(location?.search));
  }, [location.search]);

  useEffect(() => {
    async function retrieveQuestionsAndAnswers(requests, cookie) {
      requests = requests ? (Array.isArray(requests) ? requests : [requests]) : [];
      return await getQuestionsAndAnswers(requests, cookie);
    }

    if (!csrf || !query.request) {
      return;
    }

    async function retrieveRequestInfo(requests, cookie) {
       requests = requests ? (Array.isArray(requests) ? requests : [requests]) : [];
       let requestId = requests[0]
       return await getFeedbackRequestById(requestId, cookie);

    }

  retrieveRequestInfo(query.request, csrf).then((res) => {
      if (res && res.payload && res.payload.data && !res.error) {
           setRequestInfo(res.payload.data);

      } else {
       window.snackDispatch({
                type: UPDATE_TOAST,
                payload: {
                  severity: "error",
                  toast: "Failed to retrieve request information"
                },
              });
      }
  })
    retrieveQuestionsAndAnswers(query.request, csrf).then((res) => {

      if (res) {
        setQuestionsAndAnswers(res);
      } else {
        window.snackDispatch({
          type: UPDATE_TOAST,
          payload: {
            severity: "error",
            toast: "Failed to retrieve questions and answers"
          },
        });
      }
    });
  }, [csrf, query.request]);

  return (
    <div className="view-feedback-responses-page">
      <Typography
         variant='h4'
         style={{textAlign: "center", marginBottom: "1em"}}>
          <b>View Feedback for {selectProfile(state, requestInfo?.requesteeId)?.name} </b>
       </Typography>

      {questionsAndAnswers.map((question) => {
        return (
          <div className="question-responses-container"
               key={`question-id-${question.id}`}>
            <Typography
              className="question-text"
              style={{marginBottom: "0.5em", fontWeight: "bold"}}>
              Q{question.questionNumber}: {question.question}
            </Typography>
            {question?.answers?.map(answer =>
              <FeedbackResponseCard
                key={answer.id}
                responderId={answer.responder}
                answer={answer.answer}
                sentiment={answer.sentiment}/>
              )}
          </div>
        )
      })}
    </div>
  );
}

export default ViewFeedbackResponses;