import React, {useCallback, useContext, useEffect, useState} from 'react';
import {TextField, Typography} from '@material-ui/core';
import "./ViewFeedbackResponses.css";
import {makeStyles} from '@material-ui/core/styles';
import FeedbackResponseCard from "./feedback_response_card/FeedbackResponseCard";
import {getQuestionsAndAnswers} from "../../api/feedbackanswer";
import queryString from "query-string";
import {useLocation} from "react-router-dom";
import {AppContext} from "../../context/AppContext";
import {selectCsrfToken} from "../../context/selectors";
import {UPDATE_TOAST} from "../../context/actions";
import InputAdornment from "@material-ui/core/InputAdornment";
import {Group as GroupIcon, Search as SearchIcon} from "@material-ui/icons";

const useStyles = makeStyles({
  root: {
    '&:last-child': {
      paddingBottom: '16px',
    }
  },
  notFoundMessage: {
    color: "gray",
    marginTop: "3em",
    textAlign: "center"
  }
}, {name: "MuiCardContent"});

const ViewFeedbackResponses = () => {
  const classes = useStyles();
  const { state } = useContext(AppContext);
  const csrf = selectCsrfToken(state);
  const location = useLocation();
  const [query, setQuery] = useState({});
  const [questionsAndAnswers, setQuestionsAndAnswers] = useState([]);
  const [searchText, setSearchText] = useState("");
  const [filteredResponders, setFilteredResponders] = useState([]);

  const getFilteredResponses = useCallback(() => {
    if (!questionsAndAnswers) {
      return null
    } else if (questionsAndAnswers.length === 0) {
      return <Typography className={classes.notFoundMessage} variant="h5">No responses found</Typography>
    }

    let responsesToDisplay = [];

    if (responsesToDisplay.length === 0) {
      return <Typography className={classes.notFoundMessage} variant="h5">No matching responses</Typography>
    }

  }, [questionsAndAnswers]);

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

    retrieveQuestionsAndAnswers(query.request, csrf).then((res) => {
      if (res) {
        console.log(res);
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
        View Feedback for <b>Joe Johnson</b>
      </Typography>
      <div className="responses-filter-container">
        <TextField
          style={{marginRight: "3em"}}
          label="Search responses..."
          placeholder="Enter a keyword or phrase"
          value={searchText}
          onChange={(event) => setSearchText(event.target.value)}
          InputProps={{
            endAdornment: <InputAdornment style={{color: "gray"}} position="end"><SearchIcon/></InputAdornment>
          }}
        />
        <TextField
          label="Filter responders"
          InputProps={{
            endAdornment: <InputAdornment style={{color: "gray"}} position="end"><GroupIcon/></InputAdornment>
          }}
        />
      </div>

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