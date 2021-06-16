import React from "react";
import Typography from "@material-ui/core/Typography";
import CheckCircleIcon from '@material-ui/icons/CheckCircle';


import "./FeedbackRequestConfirmation.css";
import {green} from "@material-ui/core/colors";

const formatNamesAsList = (names) => {
  return names.join(", ")
}

const FeedbackRequestConfirmation = (props) => {
  return (
    <div className="request-confirmation">
      <CheckCircleIcon style={{ color: green[500] , fontSize: 400 }}>checkmark-image</CheckCircleIcon>
      <Typography variant="h3"><b>Feedback scheduled for today</b></Typography>
      <Typography className="recipients-list" variant="h6"><b>From:</b> {formatNamesAsList(["Alice", "Bob", "Eve"])}</Typography>
    </div>
  );
}

export default FeedbackRequestConfirmation;