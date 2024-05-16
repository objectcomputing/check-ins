import React, { useState } from 'react';
import {
  SentimentDissatisfied,
  SentimentNeutral,
  SentimentSatisfied,
  SentimentVeryDissatisfied,
  SentimentVerySatisfied
} from '@mui/icons-material';
import { Button, IconButton, TextField, Tooltip } from '@mui/material';

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

const Pulse = () => {
  const handleClick = () => {};
  const [comment, setComment] = useState('');
  const [score, setScore] = useState(2);

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

const PulsePage = () => {
  const submit = () => {
    alert('Submitted');
  };

  return (
    <div className="pulse-page">
      <h2>Internal Feelings</h2>
      <Pulse key="pulse-internal" />
      <h2>External Feelings</h2>
      <Pulse key="pulse-external" />
      <Button onClick={submit} variant="contained">
        Submit
      </Button>
    </div>
  );
};

PulsePage.displayName = displayName;

export default PulsePage;
