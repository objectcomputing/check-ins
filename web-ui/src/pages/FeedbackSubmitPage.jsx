import React, { useContext, useEffect, useRef, useState } from 'react';
import { styled } from '@mui/material/styles';
import FeedbackSubmissionTips from '../components/feedback_submission_tips/FeedbackSubmissionTips';
import FeedbackSubmitForm from '../components/feedback_submit_form/FeedbackSubmitForm';
import TeamMemberReview from '../components/reviews/TeamMemberReview';
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
  const tabs = query.tabs?.toString();
  const requestQuery = query.request?.toString();
  const selfRequestQuery = query.selfrequest?.toString();
  const [showTips, setShowTips] = useState(true);
  const [feedbackRequest, setFeedbackRequest] = useState(null);
  const [selfReviewRequest, setSelfReviewRequest] = useState(null);
  const [requestee, setRequestee] = useState(null);
  const [recipient, setRecipient] = useState(null);
  const [requestSubmitted, setRequestSubmitted] = useState(false);
  const [requestCanceled, setRequestCanceled] = useState(false);
  const feedbackRequestFetched = useRef(false);

  function isManager(revieweeProfile) {
    const supervisorId = revieweeProfile?.supervisorid;
    return supervisorId === currentUserId;
  }

  useEffect(() => {
    if (!requestQuery && !selfRequestQuery) {
      history.push('/checkins');
      window.snackDispatch({
        type: UPDATE_TOAST,
        payload: {
          severity: 'error',
          toast: 'No request present'
        }
      });
    }

    async function getFeedbackRequest(query, cookie) {
      if (!currentUserId || !cookie || feedbackRequestFetched.current) {
        return null;
      }

      // make call to the API
      let res = await getFeedbackRequestById(query, cookie);
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
      getFeedbackRequest(requestQuery, csrf).then(request => {
        if (request) {
          // Permission to view this feedback request will be checked later.
          if (
            request.status.toLowerCase() === 'submitted' ||
            request.submitDate
          ) {
            setRequestSubmitted(true);
            setFeedbackRequest(request);
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

    if (
      csrf &&
      currentUserId &&
      selfRequestQuery
    ) {
      getFeedbackRequest(selfRequestQuery, csrf).then(request => {
        if (request) {
          setSelfReviewRequest(request);
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

      const recipientProfile = selectProfile(
        state,
        feedbackRequest?.recipientId
      );

      // If this is our review or we are the manager of the reviewer we are
      // allowed to view this review.
      if (recipientProfile?.id != currentUserId &&
          !isManager(recipientProfile)) {
        // The current user is not the recipients's manager, we need to leave.
        history.push('/checkins');
        window.snackDispatch({
          type: UPDATE_TOAST,
          payload: {
            severity: 'error',
            toast: 'You are not authorized to perform this operation.'
          }
        });
      }
    }

    if (selfReviewRequest) {
      const recipientProfile = selectProfile(
        state,
        selfReviewRequest?.recipientId
      );
      setRecipient(recipientProfile);
    }
  }, [feedbackRequest, selfReviewRequest, state]);

  return (
    <Root className="feedback-submit-page">
      {requestCanceled ? (
        <Typography className={classes.announcement} variant="h3">
          This feedback request has been canceled.
        </Typography>
      ) : tabs || requestSubmitted || selfReviewRequest ? (
        <TeamMemberReview
          reviews={[feedbackRequest]}
          selfReview={selfReviewRequest}
          memberProfile={recipient ?? requestee}
        />
      ) : (
        <>
          {feedbackRequest &&
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
