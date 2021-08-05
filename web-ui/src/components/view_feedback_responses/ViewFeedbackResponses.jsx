import React, {useContext, useEffect, useState, useCallback} from 'react';
import {Typography} from '@material-ui/core';
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
import Search from "@material-ui/icons/Search";
import {selectProfile} from "../../context/selectors";
import TextField from "@material-ui/core/TextField";
const useStylesCardContent = makeStyles({
  root: {
    '&:last-child': {
      paddingBottom: '16px',
    }
  },

   searchField: {
      width: "100%",
      alignSelf: "start",
      marginTop: "30px"
    },
    notFoundMessage: {
      color: "gray",
      marginTop: "4em",
      textAlign: "center"
    },
}, {name: "MuiCardContent"});



const ViewFeedbackResponses = () => {
  useStylesCardContent();
  const { state } = useContext(AppContext);
  const csrf = selectCsrfToken(state);
  const classes = useStylesCardContent();
  const location = useLocation();
  const [query, setQuery] = useState({});
  const [questionsAndAnswers, setQuestionsAndAnswers] = useState([]);
  const [searchText, setSearchText] = useState("");
  const [searchFocused, setSearchFocused] = useState(false);

    const getFilteredResponses = useCallback(() => {
      if (!questionsAndAnswers) {
        return null;
      } else if (questionsAndAnswers.length === 0) {
        return <Typography className={classes.notFoundMessage} variant="h5">No responses found</Typography>
      }
      const filtered = [...questionsAndAnswers]
      let responsesToDisplay = []
      if (searchText && searchText.length > 0) {
        const queryList = searchText.split(" ");
        queryList.forEach((query) => {
          if (query.trim()) {
            filtered.forEach((question) => {
              let answerArray = question.answers
              let answerContent = ""
              let responderName = ""
              let newAnswerArray=[]
              answerArray.forEach((answer) => {
                answerContent = answer.answer
                responderName = selectProfile(state, answer.responder)?.name
                if (answerContent?.toLowerCase().includes(query.toLowerCase()) || responderName?.toLowerCase().includes(query.toLowerCase())) {
                  newAnswerArray.push(answer)
                }
              })
              let questionCopy =  {...question};
              questionCopy.answers = newAnswerArray;
              if (questionCopy.answers.length > 0) {
                responsesToDisplay.push(questionCopy)
              }

            });
          }
        });
      } else {
        responsesToDisplay =  [...questionsAndAnswers]
      } 
        if (responsesToDisplay.length === 0) {
          return <Typography className={classes.notFoundMessage} variant="h5">No matching responses</Typography>
        }


      return responsesToDisplay?.map((question) => (
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
        ));
    // eslint-disable-next-line react-hooks/exhaustive-deps
    }, [searchText, questionsAndAnswers]);


  useEffect(() => {
    setQuery(queryString.parse(location?.search));
  }, [location.search]);

  useEffect(() => {
    async function retrieveQuestionsAndAnswers(requests, cookie) {
      // requests = ['2dd2347a-c296-4986-b428-3fbf6a24ea1e', 'ab7b21d4-f88c-4494-9b0b-8541636025eb']
      requests = requests ? (Array.isArray(requests) ? requests : [requests]) : [];
      return await getQuestionsAndAnswers(requests, cookie);
    }

    if (!csrf || !query.request) {
      return;
    }

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
        View Feedback for <b>Joe Johnson</b>
      </Typography>

                <TextField
                  className={classes.searchField}
                  placeholder="Search responses..."
                  helperText={searchFocused ? "Search for answer content or responder name" : " "}
                  onFocus={() => setSearchFocused(true)}
                  onBlur={() => setSearchFocused(false)}
                  onChange={(event) => setSearchText(event.target.value)}
                  InputProps={{
                    startAdornment: (
                      <InputAdornment style={{color: "gray"}} position="start">
                        <Search/>
                      </InputAdornment>
                    ),
                  }}
                />

                  {getFilteredResponses()}

    </div>
  );
}

export default ViewFeedbackResponses;