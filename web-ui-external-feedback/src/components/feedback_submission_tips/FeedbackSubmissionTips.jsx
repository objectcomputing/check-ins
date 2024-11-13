import React from 'react';
import { styled } from '@mui/material/styles';
import Typography from '@mui/material/Typography';

import './FeedbackSubmissionTips.css';
import Button from '@mui/material/Button';
import PropTypes from 'prop-types';

const PREFIX = 'FeedbackSubmissionTips';
const classes = {
  title: `${PREFIX}-title`,
  button: `${PREFIX}-button`,
  announcement: `${PREFIX}-announcement`
};

const Root = styled('div')({
  [`& .${classes.title}`]: {
    textAlign: 'center',
    gridColumn: 2,
    ['@media (max-width: 800px)']: {
      // eslint-disable-line no-useless-computed-key
      fontSize: '34px',
      gridColumn: 1,
      textAlign: 'left'
    }
  },
  [`& .${classes.button}`]: {
    justifySelf: 'end',
    marginRight: '3em',
    ['@media (max-width: 800px)']: {
      // eslint-disable-line no-useless-computed-key
      marginRight: '0'
    }
  },
  [`& .${classes.announcement}`]: {
    textAlign: 'center',
    marginBottom: '0px',
    ['@media (max-width: 800px)']: {
      // eslint-disable-line no-useless-computed-key
      fontSize: '20px'
    }
  }
});

const sbiInfo = [
  {
    letter: 'S',
    title: 'Situation',
    description:
      'Describe the situation; be specific about when and where it occurred.'
  },
  {
    letter: 'B',
    title: 'Behavior',
    description:
      "Describe the observable behavior; don't assume you know what the other person was thinking."
  },
  {
    letter: 'I',
    title: 'Impact',
    description:
      'Describe what you thought and felt as a result of that behavior.'
  }
];

const propTypes = {
  onNextClick: PropTypes.func
};

const FeedbackSubmissionTips = props => {
  return (
    <Root className="sbi-tips-page">
      <div className="submission-tips-header">
        <Typography className={classes.title} variant="h2">
          <b>Feedback Tips</b>
        </Typography>
        <div className={classes.button}>
          <Button
            onClick={() => props.onNextClick()}
            variant="contained"
            color="primary"
          >
            Next
          </Button>
        </div>
      </div>
      <Typography className={classes.announcement} variant="h5">
        <b>
          SBI is a common approach to providing constructive and fair feedback{' '}
        </b>
      </Typography>
      <div className="submission-tips">
        {sbiInfo.map(info => (
          <div className="sbi-info" key={info.letter}>
            <div className="sbi-circle">{info.letter}</div>
            <div className="sbi-title-and-description">
              <Typography className="sbi-info-title" variant="h3">
                {info.title}
              </Typography>
              <Typography className="sbi-info-description" variant="body1">
                {info.description}
              </Typography>
            </div>
          </div>
        ))}
      </div>
    </Root>
  );
};

FeedbackSubmissionTips.propTypes = propTypes;

export default FeedbackSubmissionTips;
