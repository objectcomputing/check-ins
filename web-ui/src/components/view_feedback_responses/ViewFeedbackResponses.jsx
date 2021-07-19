import React from 'react';
import Grid from '@material-ui/core/Grid';
import TextField from '@material-ui/core/TextField';
import { Typography } from '@material-ui/core';
import "./ViewFeedbackResponses.css";
import { makeStyles } from '@material-ui/core/styles';
import Card from '@material-ui/core/Card';
import CardContent from '@material-ui/core/CardContent';
import AvatarComponent from '../avatar/Avatar'
import MoodBadIcon from '@material-ui/icons/MoodBad';
import InsertEmoticonIcon from '@material-ui/icons/InsertEmoticon';
import SentimentSatisfiedIcon from '@material-ui/icons/SentimentSatisfied';
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

    return (

        <div className="page-container">
 
            <Grid container spacing={3}>
                <Grid item xs= {12} className="text-center">
                <Typography variant='h4'>View Feedback for Joe Johnson</Typography>
                </Grid>

                {props.questions?.map((question) => {
                    return (<Grid  container item xs={11} spacing={1} key={`question-id-${question.id}`}>
                        <Typography className="question-text">Q{question.orderNum}: {question.questionContent}</Typography>
                        {props.responses?.map(answer => {
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
                                                            answer.sentiment >= 0.7 && answer.sentiment <= 1.0 ? <InsertEmoticonIcon fontSize="large"></InsertEmoticonIcon> :
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