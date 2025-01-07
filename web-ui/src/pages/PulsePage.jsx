import Cookies from 'js-cookie';
import { format } from 'date-fns';
import React, { useContext, useEffect, useState } from 'react';
import { useHistory } from 'react-router-dom';
import { Button, Checkbox, Typography } from '@mui/material';
import { downloadData, initiate } from '../api/generic.js';
import Pulse from '../components/pulse/Pulse.jsx';
import { AppContext } from '../context/AppContext';
import { selectCsrfToken, selectCurrentUser } from '../context/selectors';

import './PulsePage.css';

const center = 2; // zero-based

const PulsePage = () => {
  const { state } = useContext(AppContext);
  const currentUser = selectCurrentUser(state);
  const csrf = selectCsrfToken(state);

  const [externalComment, setExternalComment] = useState('');
  const [externalScore, setExternalScore] = useState(center);
  const [internalComment, setInternalComment] = useState('');
  const [internalScore, setInternalScore] = useState(center);
  const [pulse, setPulse] = useState(null);
  const [submittedToday, setSubmittedToday] = useState(false);
  const [submitAnonymously, setSubmitAnonymously] = useState(false);

  const today = format(new Date(), 'yyyy-MM-dd');
  const cookieName = "pulse_submitted_anonymously";
  const pulseURL = '/services/pulse-responses';

  useEffect(() => {
    const submitted = Cookies.get(cookieName);
    if (submitted) {
      setSubmittedToday(true);
      return;
    }

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

    const res = await downloadData(pulseURL, csrf, query);
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
      teamMemberId: submitAnonymously ? null : myId,
    };
    const res = await initiate(pulseURL, csrf, data);
    if (res.error) return;

    setSubmittedToday(true);
    if (submitAnonymously) {
      Cookies.set(cookieName, 'true', { expires: 1 });
    }
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
            iconRequired={true}
            score={internalScore}
            setComment={setInternalComment}
            setScore={setInternalScore}
            title="How are you feeling about work today?"
          />
          <Pulse
            key="pulse-external"
            comment={externalComment}
            score={externalScore}
            setComment={setExternalComment}
            setScore={setExternalScore}
            title="How are you feeling about life outside of work?"
          />
          <div className="submit-row">
            <Button
              style={{ marginTop: 0 }}
              onClick={submit}
              disabled={internalScore == null}
              variant="contained">
              Submit
            </Button>
            <div style={{ padding: '.3rem' }}/>
            <label>
              <Checkbox
                disableRipple
                id="submit-anonymously"
                type="checkbox"
                checked={submitAnonymously}
                onChange={(event) => setSubmitAnonymously(event.target.checked)}
              />
              Submit Anonymously
            </label>
          </div>
        </>
      )}
    </div>
  );
};

PulsePage.displayName = 'PulsePage';

export default PulsePage;
