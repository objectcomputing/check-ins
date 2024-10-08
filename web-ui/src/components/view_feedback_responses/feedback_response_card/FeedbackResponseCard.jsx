import React from 'react';
import PropTypes from 'prop-types';
import Card from '@mui/material/Card';
import CardContent from '@mui/material/CardContent';
import { Typography, IconButton } from '@mui/material';
import { Close as CloseIcon } from '@mui/icons-material';
import './FeedbackResponseCard.css';

const FeedbackResponseCard = ({ responderId, answer, inputType, sentiment, handleDenyClick }) => {
  console.log("Rendering FeedbackResponseCard");
  return (
    <Card>
      <CardContent>
        <Typography variant="h6">Responder: {responderId}</Typography>
        <Typography variant="body2">Answer: {answer}</Typography>
        <IconButton aria-label="Deny feedback request" onClick={() => {
          console.log(`Deny click for responder ID: ${responderId}`);
          handleDenyClick();
        }}>
          <CloseIcon />
        </IconButton>
      </CardContent>
    </Card>
  );
};

FeedbackResponseCard.propTypes = {
  responderId: PropTypes.string.isRequired,
  answer: PropTypes.string.isRequired,
  inputType: PropTypes.string.isRequired,
  sentiment: PropTypes.number,
  handleDenyClick: PropTypes.func.isRequired
};

export default FeedbackResponseCard;