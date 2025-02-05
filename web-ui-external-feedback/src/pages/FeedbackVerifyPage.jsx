import React, { useContext } from 'react';
import { styled } from '@mui/material/styles';
import { Button } from '@mui/material';
import { useLocation } from 'react-router-dom';
import {
  selectCsrfToken,
} from '../context/selectors';
import { resolve } from '../api/api.js';
import { AppContext } from '../context/AppContext';
import { UPDATE_TOAST } from '../context/actions';
import * as queryString from 'query-string';
import Typography from '@mui/material/Typography';

import './FeedbackRequestPage.css';

const PREFIX = 'FeedbackVerifyPage';
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

const FeedbackVerifyPage = () => {
  const { state } = useContext(AppContext);
  const csrf = selectCsrfToken(state);
  const location = useLocation();
  const query = queryString.parse(location?.search);
  const requestId = query.request?.toString();

  const submit = async () => {
    const res = await resolve({
      method: 'GET',
      url: `/verify/${requestId}`,
      headers: {
        'X-CSRF-Header': csrf,
        Accept: 'application/json',
        'Content-Type': 'application/json;charset=UTF-8'
      }
    });
    if (res?.error) {
      window.snackDispatch({
        type: UPDATE_TOAST,
        payload: {
          severity: 'error',
          toast: "Unable to process request"
        }
      });
    } else {
      window.snackDispatch({
        type: UPDATE_TOAST,
        payload: {
          severity: 'success',
          toast: "Verification email sent"
        }
      });
    }
  };

  return (
      <Root className="feedback-verify-page">
        <Typography variant="h4">
          Please click below to receive an email to begin the feedback process.
        </Typography>
        <div style={{ display: 'flex', justifyContent: 'center', margin: '4em' }}>
          <Button onClick={submit} variant="contained">
            Verify
          </Button>
        </div>
      </Root>
  );
};
export default FeedbackVerifyPage;
