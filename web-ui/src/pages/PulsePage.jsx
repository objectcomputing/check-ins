import { format } from 'date-fns';
import React, { useContext, useEffect, useState } from 'react';
import { useHistory } from 'react-router-dom';
import { Button, Typography } from '@mui/material';
import { resolve } from '../api/api.js';
import Pulse from '../components/pulse/Pulse.jsx';
import { AppContext } from '../context/AppContext';
import { selectCsrfToken, selectCurrentUser } from '../context/selectors';

import './PulsePage.css';

const center = 2; // zero-based

const PulsePage = () => {
  const { state } = useContext(AppContext);
  const currentUser = selectCurrentUser(state);
  const csrf = selectCsrfToken(state);
  const history = useHistory();

  const [externalComment, setExternalComment] = useState('');
  const [externalScore, setExternalScore] = useState(center);
  const [internalComment, setInternalComment] = useState('');
  const [internalScore, setInternalScore] = useState(center);
  const [pulse, setPulse] = useState(null);
  const [submittedToday, setSubmittedToday] = useState(false);
  const today = format(new Date(), 'yyyy-MM-dd');

  useEffect(() => {
    if (!pulse) return;

    const now = new Date();
    const [year, month, day] = pulse.submissionDate;
    setSubmittedToday(
      year === now.getFullYear() &&
        month === now.getMonth() + 1 &&
        day === now.getDate()
    );

    setInternalComment(pulse.internalFeelings ?? '');
    setExternalComment(pulse.externalFeelings ?? '');
    setInternalScore(pulse.internalScore == undefined ?
                       center : pulse.internalScore - 1);
    setExternalScore(pulse.externalScore == undefined ?
                       center : pulse.externalScore - 1);
  }, [pulse]);

  const loadTodayPulse = async () => {
    if (!csrf || !currentUser?.id) return;

    const query = {
      dateFrom: today,
      dateTo: today,
      teamMemberId: currentUser.id
    };
    const queryString = Object.entries(query)
      .map(([key, value]) => `${key}=${value}`)
      .join('&');

    const res = await resolve({
      method: 'GET',
      url: `/services/pulse-responses?${queryString}`,
      headers: {
        'X-CSRF-Header': csrf,
        Accept: 'application/json',
        'Content-Type': 'application/json;charset=UTF-8'
      }
    });
    if (res.error) return;

    // Sort pulse responses by date, latest to earliest
    const pulses = res.payload.data?.sort((a, b) => {
      const l = a.submissionDate;
      const r = b.submissionDate;
      if (r[0] == l[0]) {
        if (r[1] == l[1]) {
          return r[2] - l[2];
        } else {
          return r[1] - l[1];
        }
      } else {
        return r[0] - l[0];
      }
    });
    setPulse(pulses.at(0));
  };

  useEffect(() => {
    loadTodayPulse();
  }, [csrf, currentUser]);

  const submit = async () => {
    const myId = currentUser?.id;
    const data = {
      externalFeelings: externalComment,
      externalScore: externalScore + 1, // converts to 1-based
      internalFeelings: internalComment,
      internalScore: internalScore + 1, // converts to 1-based
      submissionDate: today,
      updatedDate: today,
      teamMemberId: myId
    };
    const res = await resolve({
      method: 'POST',
      url: '/services/pulse-responses',
      headers: {
        'X-CSRF-Header': csrf,
        Accept: 'application/json',
        'Content-Type': 'application/json;charset=UTF-8'
      },
      data
    });
    if (res.error) return;

    // Refresh browser to show that pulses where already submitted today.
    history.go(0);
  };

  return (
    <div className="pulse-page">
      {submittedToday ? (
        <Typography className="submitted-today" variant="h6">
          Thank you for submitting your pulse today!
          <br />
          Please do so again tomorrow.
        </Typography>
      ) : (
        <>
          <Pulse
            key="pulse-internal"
            comment={internalComment}
            score={internalScore}
            setComment={setInternalComment}
            setScore={setInternalScore}
            title="How are you feeling about work today? (*)"
          />
          <Pulse
            key="pulse-external"
            comment={externalComment}
            score={externalScore}
            setComment={setExternalComment}
            setScore={setExternalScore}
            title="How are you feeling about life outside of work?"
          />
          <Button onClick={submit} variant="contained">
            Submit
          </Button>
        </>
      )}
    </div>
  );
};

PulsePage.displayName = 'PulsePage';

export default PulsePage;
