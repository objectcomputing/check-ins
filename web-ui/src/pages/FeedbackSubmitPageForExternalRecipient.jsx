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
} from '../context/selectorsFeedbackExternalRecipient.js';
import { AppContext } from '../context/AppContext';
import { AppFeedbackExternalRecipientContext } from "../context/AppFeedbackExternalRecipientContext.jsx";
import { getFeedbackRequestById, getExternalRecipients } from '../api/feedback';
import { getFeedbackRequestNoCookie } from '../api/feedbackExternalRecipients';
import Typography from '@mui/material/Typography';
import { UPDATE_TOAST } from '../context/actions';
import * as queryString from 'query-string';

import './FeedbackRequestPage.css';

const PREFIX = 'FeedbackSubmitForExternalRecipientPage';
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

const FeedbackSubmitForExternalRecipientPage = () => {
  const { state, dispatch } = useContext(AppFeedbackExternalRecipientContext);
  //const csrfToken = selectCsrfToken(state);

  useEffect(() => {
    const fetchData = async () => {
      try {
        const response = await getFeedbackRequestNoCookie('93e72102-62f8-449d-a9ef-7c1ffae9a098');
        console.log("FeedbackSubmitPageForExternalRecipient, useEffect, response: ", response.payload.data);
      } catch (error) {
        console.error("FeedbackSubmitPageForExternalRecipient, useEffect, error: ",  error);
      }
    };
    fetchData();
  }, [])
  ;

  return (
      <Root >
        <h1>6:28pm ET</h1>
      </Root>
  );
};

export default FeedbackSubmitForExternalRecipientPage;
