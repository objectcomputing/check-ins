import React, {useContext} from "react";
import PropTypes from "prop-types";
import Card from "@material-ui/core/Card";
import CardContent from "@material-ui/core/CardContent";
import {Typography} from "@material-ui/core";
import TextField from "@material-ui/core/TextField";
// import SentimentIcon from "../../sentiment_icon/SentimentIcon";
import "./FeedbackResponseCard.css";
import {AppContext} from "../../../context/AppContext";
import {selectProfile} from "../../../context/selectors";
import Avatar from "@material-ui/core/Avatar";
import { getAvatarURL } from "../../../api/api.js";

const propTypes = {
  responderId: PropTypes.string.isRequired,
  answer: PropTypes.string.isRequired,
  sentiment: PropTypes.number.isRequired
}

const FeedbackResponseCard = (props) => {
  const { state } = useContext(AppContext);
  const userInfo = selectProfile(state, props.responderId);

  return (
    <Card className="response-card">
      <CardContent className="response-card-content">
        <div className="response-card-recipient-info">
          <Avatar className="avatar-photo" src={getAvatarURL(userInfo?.workEmail)}/>
          <Typography className="responder-name">{userInfo?.name}</Typography>
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
        {/*<div className="response-sentiment">*/}
        {/*  <SentimentIcon sentimentScore={props.sentiment}/>*/}
        {/*</div>*/}
      </CardContent>
    </Card>
  );
}

FeedbackResponseCard.propTypes = propTypes;

export default FeedbackResponseCard;