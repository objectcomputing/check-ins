import React from 'react';
import Grid from '@material-ui/core/Grid';
import { Link } from "react-router-dom";
import Divider from '@material-ui/core/Divider';

//note that request id will be in actual object, so you will need to get information out of request id, template id and state
let sampleQuestions = [
    {
        id: 1,
        questionContent: "How would you rate the overall technical skill of Joe Johnson? Please elaborate with examples if possible.",
        orderNum: 1,

    },
    {
        id: 2,
        questionContent: "How would you rate the overall ease of communication and dialogue with Joe Johnson? Please elaborate with examples if possible.",
        orderNum: 2,

    },


]

//note that submitter name will not be in actual returned object, but this is intermediary for time  being without api
let sampleResponses = [
    {
        answer: "Joe's implementations are easily maintainable and easy to understand. My only complaint is that he does not really label methods with descriptive Javadocs as much as he could, confusing some of our engineers.",
        questionId: 1,
        responderName: "Erin Deeds",
        sentiment: 0.6,
        
    },
    {
        answer: "Joe is an engineer. He sometimes knows how to solve some problems.",
        questionId: 1,
        responderName: "Job Johnson",
        sentiment: 0.5
    },
    {
        answer: "Joe could work better on his communication skills. I understand that he is a senior engineer " +
        "who has been developing these sort of projects for a while, but that doesn't mean he should just "+
        "implement what he thinks is best without talking to the rest of the team (especially on our side).",
        questionId: 2,
        responderName: "Erin Deeds",
        sentiment: 0.2

    },
    {
        answer: "Joe should definitely take a leaf from business and learn to actually have a dialogue with others. "
        + "He is so mysterious that I can't even figure out what I am supposed to do!",
        questionId: 2,
        responderName: "Job Johnson",
        sentiment: 0.2,
    }

]


const ViewFeedbackResponses = () => {
    const responses = [];
    const questions = [];
    const templateId = 0;

    return (
            <Grid container spacing = {3}>
                <Grid container item>
                    <Grid item xs={12}>
                    </Grid>
                    <Grid item xs={12}>
                    </Grid>
                    <Grid item xs={12}>
                    </Grid>
                </Grid>
            </Grid>
        

    )
}