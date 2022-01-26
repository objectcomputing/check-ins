import React, {useContext, useEffect, useRef, useState} from 'react';
import { styled } from '@mui/material/styles';
import {Avatar, Checkbox, Chip, TextField, Typography} from '@mui/material';
import "./ViewFeedbackResponses.css";
import FeedbackResponseCard from "./feedback_response_card/FeedbackResponseCard";
import {getQuestionsAndAnswers} from "../../api/feedbackanswer";
import {getFeedbackRequestById} from "../../api/feedback"
import queryString from "query-string";
import {useLocation} from "react-router-dom";
import {AppContext} from "../../context/AppContext";
import {selectCsrfToken, selectProfile} from "../../context/selectors";
import {UPDATE_TOAST} from "../../context/actions";
import InputAdornment from "@mui/material/InputAdornment";
import {Group as GroupIcon, Search as SearchIcon} from "@mui/icons-material";
import { Autocomplete } from '@mui/material';
import CheckBoxOutlineBlankIcon from "@mui/icons-material/CheckBoxOutlineBlank";
import CheckBoxIcon from "@mui/icons-material/CheckBox";
import {getAvatarURL} from "../../api/api";

const PREFIX = 'MuiCardContent';
const classes = {
  root: `${PREFIX}-root`,
  notFoundMessage: `${PREFIX}-notFoundMessage`,
  popupIndicator: `${PREFIX}-popupIndicator`,
  searchField: `${PREFIX}-searchField`,
  responderField: `${PREFIX}-responderField`
};

const Root = styled('div')({
  [`&.${classes.root}`]: {
    ':last-child': {
      paddingBottom: '16px',
    },
  },
  [`& .${classes.notFoundMessage}`]: {
    color: "gray",
    marginTop: "3em",
    textAlign: "center"
  },
  [`& .${classes.popupIndicator}`]: {
    transform: "none"
  },
  [`& .${classes.searchField}`]: {
    marginRight: "3em",
    width: "350px",
    ['@media (max-width: 800px)']: { // eslint-disable-line no-useless-computed-key
      marginRight: 0,
      width: "100%"
    }
  },
  [`& .${classes.responderField}`]: {
    minWidth: "500px",
    ['@media (max-width: 800px)']: { // eslint-disable-line no-useless-computed-key
      minWidth: 0,
      width: "100%"
    }
  }
});

const ViewFeedbackResponses = () => {
  const { state } = useContext(AppContext);
  const csrf = selectCsrfToken(state);
  const location = useLocation();
  const [query, setQuery] = useState({});
  const [questionsAndAnswers, setQuestionsAndAnswers] = useState([]);
  const [requestInfo, setRequestInfo] = useState({});
  const [searchText, setSearchText] = useState("");
  const [responderOptions, setResponderOptions] = useState([]);
  const [selectedResponders, setSelectedResponders] = useState([]);
  const [filteredQuestionsAndAnswers, setFilteredQuestionsAndAnswers] = useState([]);
  const retrievedQuestionsAndAnswers = useRef(false);

  console.log("Questions and answers" + JSON.stringify(questionsAndAnswers))
  console.log("Filtered questions and aswers " + JSON.stringify(filteredQuestionsAndAnswers))


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

  // Sets the options for filtering by responders
  useEffect(() => {
    let allResponders = [];
    questionsAndAnswers.forEach(({ answers }) => {
      const responders = answers.map((answer) => answer.responder);
      allResponders.push(...responders);
    });
    allResponders = [...(new Set(allResponders))]  // Remove duplicate responders

    setResponderOptions(allResponders);
  }, [state, questionsAndAnswers]);

  // Populate all responders as selected by default
  useEffect(() => {
    setSelectedResponders(responderOptions);
  }, [responderOptions]);

  useEffect(() => {
    let responsesToDisplay = [...questionsAndAnswers];
    responsesToDisplay = responsesToDisplay?.map((response) => {
      console.log("response " + JSON.stringify(response))
      // Filter based on selected responders
      let filteredAnswers = response?.answers?.filter((answer) => selectedResponders.includes(answer.responder));
      if (searchText.trim()) {
        // Filter based on search text
        filteredAnswers = filteredAnswers?.filter(({answer}) => answer?.toLowerCase().includes(searchText.trim().toLowerCase()));
      }
      return {...response, answers: filteredAnswers}
    });

    setFilteredQuestionsAndAnswers(responsesToDisplay);
  

  }, [searchText, selectedResponders]); // eslint-disable-line react-hooks/exhaustive-deps

  useEffect(() => {
    if (!retrievedQuestionsAndAnswers.current && filteredQuestionsAndAnswers.length > 0) {
      retrievedQuestionsAndAnswers.current = true;
    }
  }, [filteredQuestionsAndAnswers]);

  return (
    <Root className="view-feedback-responses-page">
      <Typography
         variant='h4'
         style={{textAlign: "center", marginBottom: "1em"}}>
          <b>View Feedback for {selectProfile(state, requestInfo?.requesteeId)?.name} </b>
       </Typography>
      <div className="responses-filter-container">
        <TextField
          className={classes.searchField}
          label="Search responses..."
          placeholder="Enter a keyword or phrase"
          helperText=" "
          value={searchText}
          onChange={(event) => setSearchText(event.target.value)}
          InputProps={{
            endAdornment: <InputAdornment style={{color: "gray"}} position="end"><SearchIcon/></InputAdornment>
          }}
        />
        <Autocomplete
          className={classes.responderField}
          classes={{popupIndicator: classes.popupIndicator}}
          multiple
          disableCloseOnSelect
          options={responderOptions}
          getOptionLabel={(responderId) => selectProfile(state, responderId).name}
          popupIcon={<GroupIcon/>}
          value={selectedResponders}
          onChange={(event, value) => setSelectedResponders(value)}
          renderOption={(responderId, { selected }) => (
            <React.Fragment>
              <Checkbox
                icon={<CheckBoxOutlineBlankIcon fontSize="small"/>}
                checkedIcon={<CheckBoxIcon fontSize="small"/>}
                color="primary"
                style={{marginRight: 8}}
                checked={selected}
              />
              {selectProfile(state, responderId)?.name}
            </React.Fragment>
          )}
          renderInput={(params) => (
            <TextField
              {...params}
              variant="standard"
              label="Filter recipients"
              helperText={`Showing responses from ${selectedResponders.length}/${responderOptions.length} recipient${responderOptions.length === 1 ? "" : "s"}`}
            />
          )}
          renderTags={(value, getTagProps) =>
            value.map((responderId, index) => {
              const profile = selectProfile(state, responderId);
              return (
                <Chip
                  avatar={<Avatar alt={`${profile.name}'s avatar`} className="large" src={getAvatarURL(profile.workEmail)}/>}
                  label={profile.name}
                  {...getTagProps({ index })}
                />
              )
            })
          }
        />
      </div>
      {filteredQuestionsAndAnswers.map((question) => {
        return (
          <div className="question-responses-container"
               key={`question-id-${question.id}`}>
            <Typography
              className="question-text"
              style={{marginBottom: "0.5em", fontWeight: "bold"}}>
              Q{question.questionNumber}: {question.question}
            </Typography>
            {question.answers.length === 0 && retrievedQuestionsAndAnswers.current && <div className="no-responses-found"><Typography variant="body1" style={{color: "gray"}}>No matching responses found</Typography></div>}
            {question.answers.length > 0 && question.answers.map(answer =>
                <FeedbackResponseCard
                  key={answer.id}
                  responderId={answer.responder}
                  answer={answer.answer}
                  sentiment={answer.sentiment}/>
              )
            }
          </div>
        )
      })}
    </Root>
  );
}

export default ViewFeedbackResponses;