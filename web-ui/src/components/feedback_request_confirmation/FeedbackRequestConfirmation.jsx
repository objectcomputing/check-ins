import React from "react";
import Typography from "@material-ui/core/Typography";
import checkmark from "./checkmark.png";

import "./FeedbackRequestConfirmation.css";

const formatNamesAsList = (names) => {
  return names.join(", ")
}

const FeedbackRequestConfirmation = (props) => {
  return (
    <div className="request-confirmation">
      <img className="checkmark-image" alt="Checkmark" src={checkmark}/>
      <Typography variant="h3"><b>Feedback scheduled for today</b></Typography>
      <Typography className="recipients-list" variant="h6"><b>From:</b> {formatNamesAsList(["Alice", "Bob", "Eve"])}</Typography>
    </div>
  );
}

export default FeedbackRequestConfirmation;