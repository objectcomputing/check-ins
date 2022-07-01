import React, {useContext} from "react";
import PropTypes from "prop-types";
import Card from "@mui/material/Card";
import CardContent from "@mui/material/CardContent";
import {Typography} from "@mui/material";
import TextField from "@mui/material/TextField";
import SentimentIcon from "../../sentiment_icon/SentimentIcon";
import "./FeedbackResponseCard.css";
import {AppContext} from "../../../context/AppContext";
import {selectProfile} from "../../../context/selectors";
import Avatar from "@mui/material/Avatar";
import { getAvatarURL } from "../../../api/api.js";

const propTypes = {
  responderId: PropTypes.string.isRequired,
  answer: PropTypes.string.isRequired,
  sentiment: PropTypes.number
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
              padding: "0.5em 1em",
              lineHeight: '1.1876em'
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