import React from 'react';
import Grid from '@material-ui/core/Grid';
import { Link } from "react-router-dom";
import Divider from '@material-ui/core/Divider';
import TextField from '@material-ui/core/TextField';
import { Typography } from '@material-ui/core';
import "./ViewFeedbackResponses.css";
import { makeStyles } from '@material-ui/core/styles';
import Card from '@material-ui/core/Card';
import CardContent from '@material-ui/core/CardContent';
import CardActions from '@material-ui/core/CardActions';
import AvatarComponent from '../avatar/Avatar'
import MoodBadIcon from '@material-ui/icons/MoodBad';
import InsertEmoticonIcon from '@material-ui/icons/InsertEmoticon';
import SentimentSatisfiedIcon from '@material-ui/icons/SentimentSatisfied';
import { Mood } from '@material-ui/icons';
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
            "who has been developing these sort of projects for a while, but that doesn't mean he should just " +
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

const useStylesCardContent = makeStyles({
    root: {
      '&:last-child': {
        paddingBottom: '16px',
      }
    }
  }, { name: "MuiCardContent" })




const ViewFeedbackResponses = () => {
    const responses = sampleResponses;
    const questions = sampleQuestions
    const templateId = 0;
     useStylesCardContent();

     function calculateFaceDisplay(sentiment) {
        console.log(sentiment)
        console.log("is this thing on???")
        if (sentiment >= 0 && sentiment < 0.3) {
            return <MoodBadIcon></MoodBadIcon>
        } else if (sentiment >= 0.3 && sentiment < 0.6) {
            return <SentimentSatisfiedIcon></SentimentSatisfiedIcon>
        } else if (sentiment >=0.6 && sentiment < 1) {
            return <InsertEmoticonIcon></InsertEmoticonIcon>
        }
    } 
  

    return (

        <div className="page-container">
            <Grid container spacing={3}>
                {questions.map((question) => {
                    return (<Grid container item xs={11}>

                        <Typography>Q{question.orderNum}: {question.questionContent}</Typography>
                        {responses.map(answer => {
                            return answer.questionId === question.id ? (
                                <Grid item xs={12} className="top-bottom-padding">
                                    <Card className="response-card justify-center">
                                        <CardContent>
                                            <Grid container spacing={0}>
                                                <Grid item xs={12}>
                                                    <Grid container
                                                        direction="row"
                                                        alignItems="center"
                                                        className="no-wrap"
                                                    >
                                                        <Grid item>
                                                            <AvatarComponent className="avatar-photo" imageUrl="../../../public/default_profile.jpg"></AvatarComponent>
                                                        </Grid>
                                                        <Grid item xs >
                                                            <Typography className="responder-name">{answer.responderName}</Typography>
                                                        </Grid>
                                                        <Grid item xs={10}>
                                                            <TextField
                                                                id="answer-disabled"
                                                                className="response-box"
                                                                multiline
                                                                defaultValue={answer.answer}
                                                                InputProps={{
                                                                    readOnly: true,
                                                                }}
                                                                variant="filled"
                                                            />
                                                        </Grid>
                                                        <Grid item xs className="small-margin">
                                                            {answer.sentiment >=0 && answer.sentiment < 0.3 ? <MoodBadIcon fontSize="large"></MoodBadIcon> :
                                                            answer.sentiment >= 0.3 && answer.sentiment < 0.7 ? <SentimentSatisfiedIcon fontSize="large"></SentimentSatisfiedIcon>:
                                                            answer.sentiment >= 0.7 && answer.sentiment <= 1.0 ? <MoodBadIcon fontSize="large"></MoodBadIcon> :
                                                            null
                                                            }
                                                        </Grid>
                                                    </Grid>
                                                </Grid>
                                            </Grid>
                                        </CardContent>
                                    </Card>
                                </Grid>
                            ) : <React.Fragment></React.Fragment>
                        })
                        }

                    </Grid>)

                })

                }

            </Grid>
        </div >

    )
}
export default ViewFeedbackResponses;