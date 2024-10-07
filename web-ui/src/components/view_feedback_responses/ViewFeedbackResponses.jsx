import React, { useContext, useEffect, useState } from 'react';
import { styled } from '@mui/material/styles';
import { Autocomplete, Avatar, Button, Checkbox, Chip, TextField, Typography, Box } from '@mui/material';
import FeedbackResponseCard from './feedback_response_card/FeedbackResponseCard';
import { getQuestionsAndAnswers } from '../../api/feedbackanswer';
import { getFeedbackRequestById } from '../../api/feedback';
import queryString from 'query-string';
import { useLocation } from 'react-router-dom';
import { AppContext } from '../../context/AppContext';
import { selectCsrfToken, selectProfile } from '../../context/selectors';
import { UPDATE_TOAST } from '../../context/actions';
import InputAdornment from '@mui/material/InputAdornment';
import { Search as SearchIcon } from '@mui/icons-material';
import { getAvatarURL } from '../../api/api';
import CheckBoxOutlineBlankIcon from '@mui/icons-material/CheckBoxOutlineBlank';
import CheckBoxIcon from '@mui/icons-material/CheckBox';
import SkeletonLoader from '../skeleton_loader/SkeletonLoader';
import ReportProblemOutlinedIcon from '@mui/icons-material/ReportProblemOutlined';

import './ViewFeedbackResponses.css';

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
      paddingBottom: '16px'
    }
  },
  [`& .${classes.notFoundMessage}`]: {
    color: 'gray',
    marginTop: '3em',
    textAlign: 'center'
  },
  [`& .${classes.popupIndicator}`]: {
    transform: 'none'
  },
  [`& .${classes.searchField}`]: {
    marginRight: '3em',
    width: '350px',
    ['@media (max-width: 800px)']: {
      marginRight: 0,
      width: '100%'
    }
  },
  [`& .${classes.responderField}`]: {
    minWidth: '500px',
    ['@media (max-width: 800px)']: {
      minWidth: 0,
      width: '100%'
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
  const [searchText, setSearchText] = useState('');
  const [responderOptions, setResponderOptions] = useState([]);
  const [selectedResponders, setSelectedResponders] = useState([]);
  const [filteredQuestionsAndAnswers, setFilteredQuestionsAndAnswers] = useState([]);
  const [isLoading, setIsLoading] = useState(true);

  useEffect(() => {
    setQuery(queryString.parse(location?.search));
  }, [location.search]);

  useEffect(() => {
    async function retrieveQuestionsAndAnswers(requests, cookie) {
      requests = requests
        ? Array.isArray(requests)
          ? requests
          : [requests]
        : [];
      const res = await getQuestionsAndAnswers(requests, cookie);

      if (res) {
        const sanitizedResponses = res.map(question => ({
          ...question,
          answers: question.answers.map(answer => ({
            ...answer,
            // Ensure blank, null, undefined, or non-string answers are handled
            answer: isEmptyOrWhitespace(answer.answer) ? ' ⚠️ No response submitted' : String(answer.answer),
          }))
        }));

        sanitizedResponses.sort((a, b) => a.questionNumber - b.questionNumber);
        setQuestionsAndAnswers(sanitizedResponses);

      } else {
        window.snackDispatch({
          type: UPDATE_TOAST,
          payload: {
            severity: 'error',
            toast: 'Failed to retrieve questions and answers'
          }
        });
      }
    }

    if (!csrf || !query.request) {
      return;
    }

    async function retrieveRequestInfo(requests, cookie) {
      requests = requests
        ? Array.isArray(requests)
          ? requests
          : [requests]
        : [];
      let requestId = requests[0];
      return await getFeedbackRequestById(requestId, cookie);
    }

    retrieveRequestInfo(query.request, csrf).then(res => {
      if (res && res.payload && res.payload.data && !res.error) {
        setRequestInfo(res.payload.data);
      } else {
        window.snackDispatch({
          type: UPDATE_TOAST,
          payload: {
            severity: 'error',
            toast: 'Failed to retrieve request information'
          }
        });
      }
    });
    retrieveQuestionsAndAnswers(query.request, csrf);
  }, [csrf, query.request]);

  // Sets the options for filtering by responders
  useEffect(() => {
    let allResponders = [];
    questionsAndAnswers.forEach(({ answers }) => {
      const responders = answers.map(answer => answer.responder);
      allResponders.push(...responders);
    });
    allResponders = [...new Set(allResponders)]; // Remove duplicate responders
    setResponderOptions(allResponders);
  }, [state, questionsAndAnswers]);

  // Populate all responders as selected by default
  useEffect(() => {
    setSelectedResponders(responderOptions);
  }, [responderOptions]);

  useEffect(() => {
    let responsesToDisplay = [...questionsAndAnswers];
    // Filter based on selected responders
    responsesToDisplay = responsesToDisplay.map(response => {
      let filteredAnswers = response.answers.filter(answer =>
        selectedResponders.includes(answer.responder)
      );

      // If no filtered answers remain after filtering recipients, display a unified error message
      if (filteredAnswers.length === 0) {
        filteredAnswers = [{ answer: 'No input due to recipient filter', responder: null }];
      }

      if (searchText.trim()) {
        // Filter based on search text
        filteredAnswers = filteredAnswers.filter(
          ({ answer }) =>
            answer &&
            answer.toLowerCase().includes(searchText.trim().toLowerCase())
        );
      }

      return { ...response, answers: filteredAnswers };
    });

    setFilteredQuestionsAndAnswers(responsesToDisplay);
  }, [searchText, selectedResponders]); // eslint-disable-line react-hooks/exhaustive-deps

  // Add "No input" for questions with inputType "NONE"
  useEffect(() => {
    if (isLoading && filteredQuestionsAndAnswers.length > 0) {
      setIsLoading(false);
    }
  }, [filteredQuestionsAndAnswers, isLoading]);

  const handleReset = () => {
    setSelectedResponders(responderOptions);
  };

  const isEmptyOrWhitespace = (text) => {
    return typeof text !== 'string' || !text.trim();
  };

  return (
    <Root className="view-feedback-responses-page">
      <Typography
        variant="h4"
        style={{ textAlign: 'center', marginBottom: '1em' }}
      >
        <b>
          View Feedback for{' '}
          {selectProfile(state, requestInfo?.requesteeId)?.name}{' '}
        </b>
      </Typography>
      <div className="responses-filter-container">
        <TextField
          className={classes.searchField}
          label="Search responses..."
          placeholder="Enter a keyword or phrase"
          helperText=" "
          value={searchText}
          onChange={event => setSearchText(event.target.value)}
          InputProps={{
            endAdornment: (
              <InputAdornment style={{ color: 'gray' }} position="end">
                <SearchIcon />
              </InputAdornment>
            )
          }}
        />
        <Autocomplete
          multiple
          className={classes.responderField}
          disableCloseOnSelect
          options={responderOptions}
          getOptionLabel={option => {
            return selectProfile(state, option)?.name;
          }}
          value={selectedResponders}
          onChange={(event, value) => setSelectedResponders(value)}
          renderOption={(props, option, { selected }) => (
            <li {...props}>
              <Checkbox
                icon={<CheckBoxOutlineBlankIcon fontSize="small" />}
                checkedIcon={<CheckBoxIcon fontSize="small" />}
                style={{ marginRight: 8 }}
                checked={selected}
              />
              {selectProfile(state, option)?.name}
            </li>
          )}
          renderInput={params => (
            <TextField
              {...params}
              variant="outlined"
              label="Filter recipients"
              helperText={`Showing responses from ${
                selectedResponders.length
              }/${responderOptions.length} recipient${
                responderOptions.length === 1 ? '' : 's'
              }`}
            />
          )}
          renderTags={(values, getTagProps) =>
            values.map((responderId, index) => {
              const profile = selectProfile(state, responderId);
              return (
                <Chip
                  key={`${responderId}-chip`}
                  avatar={
                    <Avatar
                      alt={`${profile?.name}'s avatar`}
                      className="large"
                      src={getAvatarURL(profile?.workEmail)}
                    />
                  }
                  label={profile?.name}
                  {...getTagProps({ index })}
                />
              );
            })
          }
        />
        <Button
          sx={{
            alignSelf: 'center',
            '@media (min-width: 800px)': {
              marginLeft: '1vw'
            }
          }}
          variant="contained"
          onClick={handleReset}
        >
          Reset Filter
        </Button>
      </div>
      {isLoading &&
        Array.from({ length: 10 }).map((_, index) => (
          <SkeletonLoader key={index} type="view_feedback_responses" />
        ))}
      {!isLoading &&
        filteredQuestionsAndAnswers?.map(question => {
          const questionText = question.question;
          const hasResponses = question.answers.length > 0;

          return (
            <div
              className="question-responses-container"
              key={`question-id-${question.id}`}
            >
              <Typography
                className="question-text"
                style={{ marginBottom: '0.5em', fontWeight: 'bold' }}
              >
                {questionText}
              </Typography>

              {/* If the question has no answers or inputType is "NONE" */}
              {!hasResponses || question.inputType === 'NONE' ? (
                <div className="no-responses-found">
                  <Typography
                    variant="body1"
                    style={{
                      color: 'gray',
                      display: 'flex',
                      alignItems: 'center'
                    }}
                  >
                    <ReportProblemOutlinedIcon
                      style={{ marginRight: '0.5em' }}
                    />
                    {question.inputType === 'NONE' ? 'No input available for this question.' : '⚠️ No response submitted'}
                  </Typography>
                </div>
              ) : (
                question.answers.map(answer => (
                  <FeedbackResponseCard
                    key={answer.id || answer.responder}
                    responderId={answer.responder}
                    answer={isEmptyOrWhitespace(answer.answer) ? ' ⚠️ No response submitted' : String(answer.answer)}
                    inputType={question.inputType}
                    sentiment={answer.sentiment}
                  />
                ))
              )}
            </div>
          );
        })}
    </Root>
  );
};

export default ViewFeedbackResponses;