import React, { useContext, useEffect, useRef, useState } from 'react';
import { styled } from '@mui/material/styles';
import Typography from '@mui/material/Typography';
import CheckCircleIcon from '@mui/icons-material/CheckCircle';
import {
  selectCsrfToken,
  selectCurrentUser,
  selectProfile,
  selectHasCreateFeedbackPermission,
  noPermission,
} from '../../context/selectors';
import { AppContext } from '../../context/AppContext';
import { useLocation } from 'react-router-dom';
import queryString from 'query-string';
import './FeedbackSubmitConfirmation.css';
import { green } from '@mui/material/colors';
import {getFeedbackRequestById, getRequesteeForFeedbackRequest} from '../../api/feedback';

const PREFIX = 'FeedbackSubmitConfirmation';
const classes = {
  announcement: `${PREFIX}-announcement`
};

const Root = styled('div')({
  [`& .${classes.announcement}`]: {
    textAlign: 'center',
    ['@media (max-width:820px)']: {
      // eslint-disable-line no-useless-computed-key
      fontSize: 'x-large'
    }
  }
});

const FeedbackSubmitConfirmation = props => {
  const { state } = useContext(AppContext);
  const location = useLocation();
  const query = queryString.parse(location?.search);
  const csrf = selectCsrfToken(state);
  const requestQuery = query.request?.toString();
  const [feedbackRequest, setFeedbackRequest] = useState(null);
  const feedbackRequestFetched = useRef(false);

  const [requestee, setRequestee] = useState(null);

  useEffect(() => {
    async function getFeedbackRequest(cookie) {
      if (!cookie || feedbackRequestFetched.current) {
        return null;
      }

      // make call to the API
      let res = await getFeedbackRequestById(requestQuery, cookie);
      return res.payload &&
        res.payload.data &&
        res.payload.status === 200 &&
        !res.error
        ? res.payload.data
        : null;
    }

    if (csrf && requestQuery && !feedbackRequestFetched.current) {
      getFeedbackRequest(csrf).then(request => {
        if (request) {
          setFeedbackRequest(request);
        }
      });
    }
  }, [csrf, requestQuery]);

  useEffect(() => {
    if (feedbackRequest) {
      feedbackRequestFetched.current = true;
    }

    if (feedbackRequestFetched.current) {
      getRequesteeForFeedbackRequest(feedbackRequest?.id, csrf).then(response => {
        if (response.payload && response.payload.data && !response.error) {
          setRequestee(response.payload.data);
        }
      });
    }
  }, [feedbackRequest, state]);

  return (
    <Root className="submit-confirmation">
      <CheckCircleIcon style={{ color: green[500], fontSize: '40vh' }}>
        checkmark-image
      </CheckCircleIcon>
      <Typography className={classes.announcement} variant="h3">
        Thank you for your feedback on <b>{requestee?.name}</b>
      </Typography>
    </Root>
  );
};

export default FeedbackSubmitConfirmation;
