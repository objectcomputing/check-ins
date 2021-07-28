import React from "react";
import PropTypes from "prop-types";
import Card from "@material-ui/core/Card";
import CardContent from "@material-ui/core/CardContent";
import AvatarComponent from "../../avatar/Avatar";
import {Typography} from "@material-ui/core";
import TextField from "@material-ui/core/TextField";
import SentimentIcon from "../../sentiment_icon/SentimentIcon";
import "./FeedbackResponseCard.css";

const propTypes = {
  responderName: PropTypes.string.isRequired,
  answer: PropTypes.string.isRequired,
  sentiment: PropTypes.number.isRequired
}

const FeedbackResponseCard = (props) => {
  return (
    <Card className="response-card">
      <CardContent className="response-card-content">
        <div className="response-card-recipient-info">
          <AvatarComponent className="avatar-photo" imageUrl="../../../public/default_profile.jpg"/>
          <Typography className="responder-name">{props.responderName}</Typography>
        </div>
        <TextField
          id="answer-disabled"
          className="response-box"
          multiline
          defaultValue={props.answer}
          InputProps={{
            readOnly: true,
            style: {
              padding: "0.5em 1em"
            }
          }}
          variant="filled"/>
        <div className="response-sentiment">
          <SentimentIcon sentimentScore={props.sentiment}/>
        </div>
      </CardContent>
    </Card>
  );
}

FeedbackResponseCard.propTypes = propTypes;

export default FeedbackResponseCard;