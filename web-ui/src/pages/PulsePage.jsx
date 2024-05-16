import PropTypes from 'prop-types';
import React, { useContext, useEffect, useState } from 'react';
import {
  SentimentDissatisfied,
  SentimentNeutral,
  SentimentSatisfied,
  SentimentVeryDissatisfied,
  SentimentVerySatisfied
} from '@mui/icons-material';
import { Button, IconButton, TextField, Tooltip } from '@mui/material';

import { AppContext } from '../context/AppContext';
import { selectCsrfToken, selectCurrentUser } from '../context/selectors';
import { resolve } from '../api/api.js';

import './PulsePage.css';

const colors = ['red', 'orange', 'yellow', 'lightgreen', 'green'];
const displayName = 'PulsePage';
const icons = [
  <SentimentVeryDissatisfied />,
  <SentimentDissatisfied />,
  <SentimentNeutral />,
  <SentimentSatisfied />,
  <SentimentVerySatisfied />
];
const tooltips = [
  'Very Dissatisfied',
  'Dissatisfied',
  'Neutral',
  'Satisfied',
  'Very Satisfied'
];

const propTypes = {
  initialComment: PropTypes.string,
  initialScore: PropTypes.number
};
const Pulse = ({ initialComment, initialScore }) => {
  console.log('PulsePage.jsx Pulse: initialComment =', initialComment);
  console.log('PulsePage.jsx Pulse: initialScore =', initialScore);
  const [comment, setComment] = useState(initialComment);
  console.log('PulsePage.jsx Pulse: comment =', comment);
  const [score, setScore] = useState(initialScore);

  return (
    <div className="pulse">
      <div className="icon-row">
        {icons.map((sentiment, index) => (
          <Tooltip key={`sentiment-${index}`} title={tooltips[index]} arrow>
            <IconButton
              aria-label="sentiment"
              className={index === score ? 'selected' : ''}
              onClick={() => setScore(index)}
              sx={{ color: colors[index] }}
            >
              {sentiment}
            </IconButton>
          </Tooltip>
        ))}
      </div>
      <TextField
        fullWidth
        label="Comment"
        onChange={e => {
          setComment(e.target.value);
        }}
        placeholder="Comment"
        value={comment}
      />
    </div>
  );
};
Pulse.propTypes = propTypes;

const PulsePage = () => {
  const { state } = useContext(AppContext);
  const currentUser = selectCurrentUser(state);
  const csrf = selectCsrfToken(state);

  const [pulse, setPulse] = useState(null);
  console.log('PulsePage.jsx: pulse =', pulse);

  const loadPulse = async () => {
    const myId = currentUser?.id;
    try {
      const res = await resolve({
        method: 'GET',
        url: '/services/pulse-responses',
        headers: {
          'X-CSRF-Header': csrf,
          Accept: 'application/json',
          'Content-Type': 'application/json;charset=UTF-8'
        }
      });
      if (res.error) throw new Error(res.error.message);
      const pulses = res.payload.data;
      // TODO: Currently these objects only contain the comment text,
      //       not the 1 - 5 scores.
      //       Story 2345 that Syd is working will add those.
      console.log('PulsePage.jsx loadPulse: pulses =', pulses);
      // TODO: Can we assume that the first object in the pulses array
      //       contains the most recent data ?
      setPulse(pulses[0]);
    } catch (err) {
      console.error('PulsePage.jsx loadPulse:', err);
    }
  };

  useEffect(() => {
    loadPulse();
  }, []);

  const submit = () => {
    alert('Submitted');
  };

  return (
    <div className="pulse-page">
      <h2>Internal Feelings</h2>
      <Pulse
        key="pulse-internal"
        initialComment={pulse?.internalFeelings ?? ''}
        initialScore={2}
      />
      <h2>External Feelings</h2>
      <Pulse
        key="pulse-external"
        initialComment={pulse?.externalFeelings ?? ''}
        initialScore={3}
      />
      <Button onClick={submit} variant="contained">
        Submit
      </Button>
    </div>
  );
};

PulsePage.displayName = displayName;

export default PulsePage;
