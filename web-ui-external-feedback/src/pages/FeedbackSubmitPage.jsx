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
import {getRequesteeForFeedbackRequest, getFeedbackRequestByIdForExternalRecipient} from '../api/feedback';
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
  const location = useLocation();
  const history = useHistory();
  const query = queryString.parse(location?.search);
  const requestId = query.request?.toString();
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
      if (!cookie || feedbackRequestFetched.current) {
        return null;
      }

      // make call to the API
      let res = await getFeedbackRequestByIdForExternalRecipient(requestId, cookie);
      return res.payload &&
      res.payload.data &&
      res.payload.status === 200 &&
      !res.error
          ? res.payload.data
          : null;
    }

    if (csrf && requestQuery && !feedbackRequestFetched.current) {
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

    if (csrf && selfRequestQuery) {
      getFeedbackRequest(selfRequestQuery, csrf).then(request => {
        if (request) {
          setSelfReviewRequest(request);
        }
      });
    }
  }, [csrf, requestQuery, history]);

  useEffect(() => {
    const fetchDataRequestee = async () => {
      if (feedbackRequest) {
        feedbackRequestFetched.current = true;
      }
      if (feedbackRequestFetched.current) {
        const res = await getRequesteeForFeedbackRequest(feedbackRequest?.id, csrf);
        const requesteeData = res.payload && res.payload.data && res.payload.status === 200 && !res.error
            ? res.payload.data
            : null
        ;
        setRequestee(requesteeData)
      }
    };

    fetchDataRequestee();
  }, [csrf, feedbackRequest, state])
  ;

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
                      <FeedbackSubmissionTips onNextClick={() =>
                          setShowTips(false)
                      } />
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
