import React, { useState } from 'react';
import {
  SentimentDissatisfied,
  SentimentNeutral,
  SentimentSatisfied,
  SentimentVeryDissatisfied,
  SentimentVerySatisfied
} from '@mui/icons-material';
import { IconButton, Tooltip } from '@mui/material';
// TODO: Should this use the SentimentIcon component?

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
  const [score, setScore] = useState(2);

  return (
    <div className="pulse">
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
  );
};

const PulsePage = () => {
  return (
    <div className="pulse-page">
      <h2>Internal</h2>
      <Pulse key="pulse-internal" />
      <h2>External</h2>
      <Pulse key="pulse-external" />
    </div>
  );
};

PulsePage.displayName = displayName;

export default PulsePage;
