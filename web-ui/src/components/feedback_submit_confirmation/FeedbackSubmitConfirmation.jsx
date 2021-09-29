import React, {useContext, useEffect, useRef, useState} from "react";
import Typography from "@mui/material/Typography";
import CheckCircleIcon from '@mui/icons-material/CheckCircle';
import makeStyles from '@mui/styles/makeStyles';
import {selectCsrfToken, selectCurrentUser, selectProfile} from "../../context/selectors";
import { AppContext } from "../../context/AppContext";
import { useLocation } from 'react-router-dom';
import queryString from 'query-string';
import "./FeedbackSubmitConfirmation.css";
import { green } from "@mui/material/colors";
import {getFeedbackRequestById} from "../../api/feedback";

const useStyles = makeStyles({
  announcement: {
    textAlign: "center",
    ['@media (max-width:820px)']: { // eslint-disable-line no-useless-computed-key
      fontSize: "x-large",
    },
  },

  checkmark: {
    ['@media (max-width:820px)']: { // eslint-disable-line no-useless-computed-key
      width: "65%",
    },
  },

});

const FeedbackSubmitConfirmation = (props) => {
  const classes = useStyles();
  const { state } = useContext(AppContext);
  const location = useLocation();
  const query = queryString.parse(location?.search);
  const csrf = selectCsrfToken(state);
  const currentUserId = selectCurrentUser(state)?.id;
  const requestQuery = query.request?.toString();
  const [feedbackRequest, setFeedbackRequest] = useState(null);
  const feedbackRequestFetched = useRef(false);

  const [requestee, setRequestee] = useState(null)

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
        ? res.payload.data
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
      feedbackRequestFetched.current = true;
    }

    if (feedbackRequestFetched.current) {
      const requesteeProfile = selectProfile(state, feedbackRequest?.requesteeId);
      setRequestee(requesteeProfile);
    }
  }, [feedbackRequest, state]);

  return (
    <div className="submit-confirmation">
      <CheckCircleIcon style={{ color: green[500], fontSize: '40vh' }}>checkmark-image</CheckCircleIcon>
      <Typography className={classes.announcement} variant="h3">Thank you for your feedback on <b>{requestee?.name}</b></Typography>
    </div>
  );
}

export default FeedbackSubmitConfirmation;