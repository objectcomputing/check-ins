import React from "react";
import Typography from "@material-ui/core/Typography";
import CheckCircleIcon from '@material-ui/icons/CheckCircle';
import {makeStyles} from '@material-ui/core/styles';


import "./FeedbackRequestConfirmation.css";
import {green} from "@material-ui/core/colors";

const formatNamesAsList = (names) => {
  return names.join(", ")
}
const useStyles = makeStyles({
  announcement: {
    textAlign: "center",
      ['@media (max-width:820px)']: { // eslint-disable-line no-useless-computed-key
        fontSize: "x-large",
      },
  },

  checkmark: {
    ['@media (max-width:820px)']: { // eslint-disable-line no-useless-computed-key
      width: "65%",
    },
  },

});

const FeedbackRequestConfirmation = (props) => {
  const classes = useStyles();
  return (
    <div className="request-confirmation">
      <CheckCircleIcon className={classes.checkmark} style={{ color: green[500] , fontSize: '40vh' }}>checkmark-image</CheckCircleIcon>
      <Typography className={classes.announcement} variant="h3"><b>Feedback scheduled for today</b></Typography>
      <Typography className="recipients-list" variant="h6"><b>From:</b> {formatNamesAsList(["Alice", "Bob", "Eve"])}</Typography>
    </div>
  );
}

export default FeedbackRequestConfirmation;