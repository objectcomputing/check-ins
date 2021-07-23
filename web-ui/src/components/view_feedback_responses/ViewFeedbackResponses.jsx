import React from 'react';
import Grid from '@material-ui/core/Grid';
import TextField from '@material-ui/core/TextField';
import { Typography } from '@material-ui/core';
import "./ViewFeedbackResponses.css";
import { makeStyles } from '@material-ui/core/styles';
import Card from '@material-ui/core/Card';
import CardContent from '@material-ui/core/CardContent';
import AvatarComponent from '../avatar/Avatar'
import SentimentIcon from "../sentiment_icon/SentimentIcon";
//note that request id will be in actual object, so you will need to get information out of request id, template id and state


const useStylesCardContent = makeStyles({
    root: {
      '&:last-child': {
        paddingBottom: '16px',
      }
    }
  }, { name: "MuiCardContent" })


const ViewFeedbackResponses = (props) => {
     useStylesCardContent();
     let questions =[
        {
            id: 1,
            questionContent: "What is your current knowledge about opossums?",
            orderNum: 1,
    
        },
        {
            id: 2,
            questionContent: "Do you think opossums are misunderstood creatures? Why?",
            orderNum: 2,
    
        },
        {
          id: 3,
          questionContent: "If you knew that opossums didn't carry rabies or other common 'vermin' diseases, are often very docile, and can eat up to 5,000 ticks a season, would your opinion change about opossums?",
          orderNum: 3,
    
      },
    
    
    ]
    //note that submitter name will not be in actual returned object, but this is intermediary for time  being without api
    let responses= [
        {
            answer: "I don't know that much about opossums",
            questionId: 1,
            responderName: "Erin Deeds",
            sentiment: 0,
    
        },
        {
            answer: "I love opossums. I have rehabilitated baby opossums for 25 years, and I intend to do so until my last day!",
            questionId: 1,
            responderName: "Job Johnson",
            sentiment: 0.8
        },
    
        {
            answer: "I always thought they were sort of nasty creatures...",
            questionId: 2,
            responderName: "Erin Deeds",
            sentiment: -0.7
    
        },
        {
            answer: "Opossums are very misunderstood. People think they are dirty and diseased, but their drooling and hissing is a defense mechanism. They eat all kinds of pests, like ticks and mice, keeping disease down. They are wonderful critters!",
            questionId: 2,
            responderName: "Job Johnson",
            sentiment: 0.9,
        },
        {
          answer: "I never knew that about opossums. I think my opinion of them is a little better now.",
          questionId: 3,
          responderName: "Erin Deeds",
          sentiment: 0.1,
    
      },
      {
          answer: "I already knew that opossums were great.",
          questionId: 3,
          responderName: "Job Johnson",
          sentiment: 0.7,
      }
    
    ]

    return (

        <div className="page-container">
            <Grid container spacing={3}>
                <Grid item xs= {12} className="text-center">
                <Typography variant='h4'>View Feedback for Joe Johnson</Typography>
                </Grid>

                {questions?.map((question) => {
                    return (<Grid  container item xs={11} spacing={1} key={`question-id-${question.id}`}>
                        <Typography className="question-text">Q{question.orderNum}: {question.questionContent}</Typography>
                        {responses?.map(answer => {
                            return answer.questionId === question.id ? (
                                <Grid item xs={12} className="top-bottom-padding" key={`answer-id-${answer.id}`}>
                                    <Card className="response-card justify-center">
                                        <CardContent>
                                            <Grid container spacing={5}>
                                                <Grid item xs={12}>
                                                    <Grid container
                                                        direction="row"
                                                        alignItems="center"
                                                    >
                                                        <Grid item>
                                                            <AvatarComponent className="avatar-photo" imageUrl="../../../public/default_profile.jpg"/>
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
                                                          <SentimentIcon sentimentScore={answer.sentiment}/>
                                                        </Grid>
                                                    </Grid>
                                                </Grid>
                                            </Grid>
                                        </CardContent>
                                    </Card>
                                </Grid>
                            ) : <React.Fragment/>
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