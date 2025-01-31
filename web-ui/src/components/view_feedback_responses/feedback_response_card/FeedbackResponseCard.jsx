import React, { useContext } from 'react';
import PropTypes from 'prop-types';
import Card from '@mui/material/Card';
import CardContent from '@mui/material/CardContent';
import { Typography } from '@mui/material';
import './FeedbackResponseCard.css';
import { AppContext } from '../../../context/AppContext';
import Avatar from '@mui/material/Avatar';
import { getAvatarURL } from '../../../api/api.js';
import FeedbackAnswerInput from '../../feedback_answer_input/FeedbackAnswerInput';

const propTypes = {
  responderName: PropTypes.string.isRequired,
  responderEmail: PropTypes.string.isRequired,
  answer: PropTypes.string, // Allow answer to be null or undefined
  inputType: PropTypes.string.isRequired,
  sentiment: PropTypes.number
};

const FeedbackResponseCard = props => {
  const { state } = useContext(AppContext);

  const getFormattedAnswer = () => {
    if (props.inputType === 'NONE') {
      return null; // Return null to display nothing
    }

    // Return fallback if the answer is null, undefined, or empty
    if (props.answer === null || props.answer === undefined || !props.answer.trim()) {
      return '⚠️ No response submitted';
    }

    return props.answer;
  };

  return (
    <Card className="response-card">
      <CardContent className="response-card-content">
        <div className="response-card-recipient-info">
          <Avatar
            className="avatar-photo"
            src={getAvatarURL(props.responderEmail)}
          />
          <Typography className="responder-name">{props.responderName}</Typography>
        </div>
        {props.inputType !== 'NONE' && (
          <FeedbackAnswerInput
            inputType={props.inputType}
            readOnly
            answer={getFormattedAnswer()} // Ensure the proper message is displayed
          />
        )}
      </CardContent>
    </Card>
  );
};

FeedbackResponseCard.propTypes = propTypes;

export default FeedbackResponseCard;
