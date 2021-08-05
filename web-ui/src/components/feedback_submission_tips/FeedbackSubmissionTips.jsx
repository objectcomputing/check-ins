import React from "react";
import Typography from "@material-ui/core/Typography";
import {makeStyles} from '@material-ui/core/styles';

import "./FeedbackSubmissionTips.css";
import Button from "@material-ui/core/Button";
import PropTypes from "prop-types";

const useStyles = makeStyles({
  title: {
    textAlign: "center",
    gridColumn: 2,
    ['@media (max-width: 800px)']: { // eslint-disable-line no-useless-computed-key
      fontSize: "34px",
      gridColumn: 1,
      textAlign: "left",

    }
  },

  button: {
    justifySelf: "end",
    marginRight: "3em",
    ['@media (max-width: 800px)']: { // eslint-disable-line no-useless-computed-key
      marginRight: "0",
    }
    },

  announcement: {
    textAlign: "center",
    marginBottom: "0px",
    ['@media (max-width: 800px)']: { // eslint-disable-line no-useless-computed-key
      fontSize: "20px",
    }
  }
});

const sbiInfo = [
  {
    letter: "S",
    title: "Situation",
    description: "Describe the situation; be specific about when and where it occurred."
  },
  {
    letter: "B",
    title: "Behavior",
    description: "Describe the observable behavior; don't assume you know what the other person was thinking."
  },
  {
    letter: "I",
    title: "Impact",
    description: "Describe what you thought and felt as a result of that behavior."
  }
];

const propTypes = {
  onNextClick: PropTypes.func
}

const FeedbackSubmissionTips = (props) => {
  const classes = useStyles();
  return (
    <div className="sbi-tips-page">
      <div className="header">
        <Typography className={classes.title} variant="h2"><b>Feedback Tips</b></Typography>
        <div className={classes.button}>
          <Button
            onClick={() => props.onNextClick()}
            variant="contained"
            color="primary">
            Next
          </Button>
        </div>
      </div>
      <Typography className={classes.announcement} variant="h5"><b>SBI is a common approach to providing constructive and fair feedback </b></Typography>
      <div className="submission-tips">
        {sbiInfo.map((info) => (
          <div className="sbi-info" key={info.letter}>
            <div className="sbi-circle">{info.letter}</div>
            <div className="sbi-title-and-description">
              <Typography className="sbi-info-title" variant="h3">{info.title}</Typography>
              <Typography className="sbi-info-description" variant="body1">{info.description}</Typography>
            </div>
          </div>
        ))}
      </div>
    </div>
  );
}

FeedbackSubmissionTips.propTypes = propTypes;

export default FeedbackSubmissionTips;