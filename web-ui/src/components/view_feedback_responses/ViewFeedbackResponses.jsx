import React, {useContext, useEffect, useState} from 'react';
import {Typography} from '@material-ui/core';
import "./ViewFeedbackResponses.css";
import {makeStyles} from '@material-ui/core/styles';
import FeedbackResponseCard from "./feedback_response_card/FeedbackResponseCard";
import {getQuestionsAndAnswers} from "../../api/feedbackanswer";
import queryString from "query-string";
import {useLocation} from "react-router-dom";
import {AppContext} from "../../context/AppContext";
import {selectCsrfToken} from "../../context/selectors";
//note that request id will be in actual object, so you will need to get information out of request id, template id and state

const useStylesCardContent = makeStyles({
  root: {
    '&:last-child': {
      paddingBottom: '16px',
    }
  }
}, {name: "MuiCardContent"})


let questions = [
  {
    id: 1,
    questionContent: "What is your current knowledge about opossums?",
    orderNum: 1,
  },
  {
    id: 2,
    questionContent: "Do you think opossums are misunderstood creatures? Why?",
    orderNum: 2,
  },
  {
    id: 3,
    questionContent: "If you knew that opossums didn't carry rabies or other common 'vermin' diseases, are often very docile, and can eat up to 5,000 ticks a season, would your opinion change about opossums?",
    orderNum: 3,

  },
];

//note that submitter name will not be in actual returned object, but this is intermediary for time  being without api
let responses = [
  {
    id: 1,
    answer: "I don't know that much about opossums",
    questionId: 1,
    responderName: "Erin Deeds",
    sentiment: 0,

  },
  {
    id: 2,
    answer: "I love opossums. I have rehabilitated baby opossums for 25 years, and I intend to do so until my last day!",
    questionId: 1,
    responderName: "Job Johnson",
    sentiment: 0.8
  },
  {
    id: 3,
    answer: "I always thought they were sort of nasty creatures...",
    questionId: 2,
    responderName: "Erin Deeds",
    sentiment: -0.7

  },
  {
    id: 4,
    answer: "Opossums are very misunderstood. People think they are dirty and diseased, but their drooling and hissing is a defense mechanism. They eat all kinds of pests, like ticks and mice, keeping disease down. They are wonderful critters!",
    questionId: 2,
    responderName: "Job Johnson",
    sentiment: 0.9,
  },
  {
    id: 5,
    answer: "I never knew that about opossums. I think my opinion of them is a little better now.",
    questionId: 3,
    responderName: "Erin Deeds",
    sentiment: 0.1,

  },
  {
    id: 6,
    answer: "I already knew that opossums were great.",
    questionId: 3,
    responderName: "Job Johnson",
    sentiment: 0.7,
  }
];

const ViewFeedbackResponses = () => {
  useStylesCardContent();
  const { state } = useContext(AppContext);
  const csrf = selectCsrfToken(state);
  const location = useLocation();
  const [query, setQuery] = useState({});

  useEffect(() => {
    setQuery(queryString.parse(location?.search));
  }, [location.search]);

  useEffect(() => {
    async function retrieveQuestionsAndAnswers(requests, cookie) {
      let res = await getQuestionsAndAnswers(requests, cookie);
      return res.payload && res.payload.data && !res.error
        ? data
        : null;
    }

    retrieveQuestionsAndAnswers(query.request, csrf);
  }, []);

  return (
    <div className="view-feedback-responses-page">
      <Typography
        variant='h4'
        style={{textAlign: "center", marginBottom: "1em"}}>
        View Feedback for <b>Joe Johnson</b>
      </Typography>

      {questions?.map((question) => {
        return (
          <div className="question-responses-container"
               key={`question-id-${question.id}`}>
            <Typography
              className="question-text"
              style={{marginBottom: "0.5em", fontWeight: "bold"}}>
              Q{question.orderNum}: {question.questionContent}
            </Typography>
            {responses?.map(answer => {
              return answer.questionId === question.id ? (
                <FeedbackResponseCard
                  responderName={answer.responderName}
                  answer={answer.answer}
                  sentiment={answer.sentiment}/>
              ) : <React.Fragment key={`answer-id-${answer.id}`}/>
            })}
        </div>)
      })}
    </div>
  );
}

export default ViewFeedbackResponses;