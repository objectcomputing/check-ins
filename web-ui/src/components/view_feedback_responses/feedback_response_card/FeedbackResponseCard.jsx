import React, {useContext} from "react";
import PropTypes from "prop-types";
import Card from "@mui/material/Card";
import CardContent from "@mui/material/CardContent";
import {Typography} from "@mui/material";
import "./FeedbackResponseCard.css";
import {AppContext} from "../../../context/AppContext";
import {selectProfile} from "../../../context/selectors";
import Avatar from "@mui/material/Avatar";
import { getAvatarURL } from "../../../api/api.js";
import FeedbackAnswerInput from "../../feedback_answer_input/FeedbackAnswerInput";

const propTypes = {
  responderId: PropTypes.string.isRequired,
  answer: PropTypes.string.isRequired,
  inputType: PropTypes.string.isRequired,
  sentiment: PropTypes.number
};

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
        <FeedbackAnswerInput
          inputType={props.inputType}
          readOnly
          answer={props.answer}
        />
      </CardContent>
    </Card>
  );
}

FeedbackResponseCard.propTypes = propTypes;

export default FeedbackResponseCard;