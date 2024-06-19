import React, { useContext, useEffect, useRef, useState } from 'react';
import { styled } from '@mui/material/styles';
import FeedbackSubmissionTips from '../components/feedback_submission_tips/FeedbackSubmissionTips';
import FeedbackSubmitForm from '../components/feedback_submit_form/FeedbackSubmitForm';
import { useHistory, useLocation } from 'react-router-dom';
import {
  selectCsrfToken,
  selectCurrentUser,
  selectProfile
} from '../context/selectors';
import { AppContext } from '../context/AppContext';
import { getFeedbackRequestById } from '../api/feedback';
import Typography from '@mui/material/Typography';
import { UPDATE_TOAST } from '../context/actions';
import * as queryString from 'query-string';

import './FeedbackRequestPage.css';

const PREFIX = 'FeedbackSubmitPage';
const classes = {
  announcement: `${PREFIX}-announcement`
};

const Root = styled('div')({
  [`& .${classes.announcement}`]: {
    textAlign: 'center',
    marginTop: '3em',
    ['@media (max-width: 800px)']: {
      // eslint-disable-line no-useless-computed-key
      fontSize: '22px'
    }
  }
});

const FeedbackSubmitPage = () => {
  const { state } = useContext(AppContext);
  const csrf = selectCsrfToken(state);
  const currentUserId = selectCurrentUser(state)?.id;
  const location = useLocation();
  const history = useHistory();
  const query = queryString.parse(location?.search);
  const requestQuery = query.request?.toString();
  const [showTips, setShowTips] = useState(true);
  const [feedbackRequest, setFeedbackRequest] = useState(null);
  const [requestee, setRequestee] = useState(null);
  const [requestSubmitted, setRequestSubmitted] = useState(false);
  const [requestCanceled, setRequestCanceled] = useState(false);
  const feedbackRequestFetched = useRef(false);

  useEffect(() => {
    if (!requestQuery) {
      history.push('/checkins');
      window.snackDispatch({
        type: UPDATE_TOAST,
        payload: {
          severity: 'error',
          toast: 'No request present'
        }
      });
    }
    async function getFeedbackRequest(cookie) {
      if (!currentUserId || !cookie || feedbackRequestFetched.current) {
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

    if (
      csrf &&
      currentUserId &&
      requestQuery &&
      !feedbackRequestFetched.current
    ) {
      getFeedbackRequest(csrf).then(request => {
        if (request) {
          if (request.recipientId !== currentUserId) {
            history.push('/checkins');
            window.snackDispatch({
              type: UPDATE_TOAST,
              payload: {
                severity: 'error',
                toast: 'You are not authorized to perform this operation.'
              }
            });
          } else if (
            request.status.toLowerCase() === 'submitted' ||
            request.submitDate
          ) {
            setRequestSubmitted(true);
          } else if (request.status.toLowerCase() === 'canceled') {
            setRequestCanceled(true);
          } else {
            setFeedbackRequest(request);
          }
        } else {
          history.push('/checkins');
          window.snackDispatch({
            type: UPDATE_TOAST,
            payload: {
              severity: 'error',
              toast: "Can't find feedback request with that ID"
            }
          });
        }
      });
    }
  }, [csrf, currentUserId, requestQuery, history]);

  useEffect(() => {
    if (feedbackRequest) {
      feedbackRequestFetched.current = true;
    }

    if (feedbackRequestFetched.current) {
      const requesteeProfile = selectProfile(
        state,
        feedbackRequest?.requesteeId
      );
      setRequestee(requesteeProfile);
    }
  }, [feedbackRequest, state]);

  return (
    <Root className="feedback-submit-page">
      {requestCanceled ? (
        <Typography className={classes.announcement} variant="h3">
          This feedback request has been canceled.
        </Typography>
      ) : requestSubmitted ? (
        <Typography className={classes.announcement} variant="h3">
          You have already submitted this feedback form. Thank you!
        </Typography>
      ) : (
        <>
          {feedbackRequestFetched.current &&
            (showTips ? (
              <FeedbackSubmissionTips onNextClick={() => setShowTips(false)} />
            ) : (
              <FeedbackSubmitForm
                requesteeName={requestee?.name}
                requestId={requestQuery}
                request={feedbackRequest}
              />
            ))}
        </>
      )}
    </Root>
  );
};
export default FeedbackSubmitPage;
