import React from "react";
import Card from "@material-ui/core/Card";
import CardContent from "@material-ui/core/CardContent";
import AvatarComponent from "../../avatar/Avatar";
import {Typography} from "@material-ui/core";
import TextField from "@material-ui/core/TextField";
import SentimentIcon from "../../sentiment_icon/SentimentIcon";
import PropTypes from "prop-types";

import "./FeedbackResponse.css";

const propTypes = {
    responderName: PropTypes.string.isRequired,
    answer: PropTypes.string.isRequired,
    sentiment: PropTypes.number.isRequired
}

const FeedbackResponse = (props) => {

    return (
        <Card>
            <CardContent className="response-card">
                <div className="response-card-responder-info">
                    <AvatarComponent className="avatar-photo" imageUrl="../../../public/default_profile.jpg"/>
                    <Typography className="responder-name">{props.responderName}</Typography>
                </div>
                <TextField
                    id="answer-disabled"
                    className="response-box"
                    multiline
                    defaultValue={props.answer}
                    variant="filled"
                    InputProps={{
                        readOnly: true,
                        style: {
                            padding: "0.5em 1em"
                        }
                    }}/>
                <div className="sentiment-icon">
                    <SentimentIcon sentimentScore={props.sentiment}/>
                </div>
            </CardContent>
        </Card>
    );

}

FeedbackResponse.propTypes = propTypes;

export default FeedbackResponse;