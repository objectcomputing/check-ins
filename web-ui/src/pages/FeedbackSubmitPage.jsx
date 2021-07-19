import React, {useContext, useEffect, useRef} from "react";
import { useState } from 'react'
import queryString from "query-string";
import "./FeedbackRequestPage.css";
import FeedbackSubmissionTips from "../components/feedback_submission_tips/FeedbackSubmissionTips";
import FeedbackSubmitForm from "../components/feedback_submit_form/FeedbackSubmitForm";
import {useLocation} from "react-router-dom";
import {selectCsrfToken, selectCurrentUser, selectProfile} from "../context/selectors";
import {AppContext} from "../context/AppContext";
import {getFeedbackRequestById} from "../api/feedback";

const FeedbackSubmitPage = () => {
  const { state } = useContext(AppContext);
  const csrf = selectCsrfToken(state);
  const currentUserId = selectCurrentUser(state)?.id;
  const location = useLocation();
  const query = queryString.parse(location?.search);
  const requestQuery = query.request?.toString();

  const [showTips, setShowTips] = useState(true);
  const [feedbackRequest, setFeedbackRequest] = useState(null);
  const [requestee, setRequestee] = useState(null);
  const feedbackRequestFetched = useRef(false);

  useEffect(() => {
    async function getFeedbackRequest(cookie) {
      if (!currentUserId || !cookie || feedbackRequestFetched.current) {
        return null;
      }

      // make call to the API
      let res = await getFeedbackRequestById(requestQuery, cookie);
      return (
        res.payload &&
        res.payload.data &&
        res.payload.status === 200 &&
        !res.error)
        ? res.payload.data[0]
        : null;
    }

    if (csrf && currentUserId && requestQuery && !feedbackRequestFetched.current) {
      getFeedbackRequest(csrf).then((request) => {
        if (request) {
          setFeedbackRequest(request);
        }
      });
    }
  }, [csrf, currentUserId, requestQuery]);

  useEffect(() => {
    if (feedbackRequest) {
      feedbackRequestFetched.current = true
    }

    if (feedbackRequestFetched.current) {
      const requesteeProfile = selectProfile(state, feedbackRequest?.requesteeId);
      setRequestee(requesteeProfile);
    }
  }, [feedbackRequest, state]);

  return (
    <div className="feedback-submit-page">
      {showTips ?
        <FeedbackSubmissionTips onNextClick={() => setShowTips(false)} /> :
        <FeedbackSubmitForm requesteeName={requestee?.name}/>
      }
    </div>
  );
};
export default FeedbackSubmitPage;