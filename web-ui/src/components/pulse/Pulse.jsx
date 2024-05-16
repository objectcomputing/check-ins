import PropTypes from 'prop-types';
import React from 'react';
import {
  SentimentDissatisfied,
  SentimentNeutral,
  SentimentSatisfied,
  SentimentVeryDissatisfied,
  SentimentVerySatisfied
} from '@mui/icons-material';
import {
  Button,
  IconButton,
  TextField,
  Tooltip,
  Typography
} from '@mui/material';

import './Pulse.css';

const colors = ['red', 'orange', 'yellow', 'lightgreen', 'green'];
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
  comment: PropTypes.string,
  score: PropTypes.number,
  setComment: PropTypes.func,
  setScore: PropTypes.func,
  title: PropTypes.string
};
const Pulse = ({ comment, score, setComment, setScore, title }) => (
  <div className="pulse">
    <Typography variant="h6">{title}</Typography>
    <div className="icon-row">
      {icons.map((sentiment, index) => (
        <Tooltip key={`sentiment-${index}`} title={tooltips[index]} arrow>
          <IconButton
            aria-label="sentiment"
            className={index === score ? 'selected' : ''}
            data-testid={`score-button-${index}`}
            onClick={() => setScore(index)}
            sx={{ color: colors[index] }}
          >
            {sentiment}
          </IconButton>
        </Tooltip>
      ))}
    </div>
    <TextField
      data-testid={`comment-input`}
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
Pulse.displayName = 'Pulse';
Pulse.propTypes = propTypes;

export default Pulse;
