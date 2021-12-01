import React, { useContext, useEffect, useState } from "react";

import { getAvatarURL } from "../../api/api.js";
import { AppContext } from "../../context/AppContext";
import { selectProfile } from "../../context/selectors";
import { getFeedbackRequestsByRequestee } from "../../api/feedback.js";
import { getFeedbackTemplateWithQuestions } from "../../api/feedbacktemplate.js";
import { getAnswersFromRequest } from "../../api/feedbackanswer";

import {
  Avatar,
  Card,
  CardHeader,
  CardContent,
  Typography,
  List,
  ListItem,
  Grid,
} from "@mui/material";

const QuestionResponse = ({fromProfile, responseText}) => (
    <Grid container spacing={0} style={{marginBottom:"1em"}}>
      <Grid item xs={12}>
        <Grid
          container
          direction="row"
          alignItems="center"
          className="no-wrap"
        >
          <Grid item>
            <Avatar style={{ marginRight: "1em" }} src={getAvatarURL(fromProfile?.workEmail)} />
          </Grid>
          <Grid item xs={3} className="small-margin">
            <Typography className="person-name">
              {fromProfile?.name}
            </Typography>
            <Typography className="position-text">
              {fromProfile?.title}
            </Typography>
          </Grid>
          <Grid item xs={8}>
            <Typography className="dark-gray-text">
              {responseText}
            </Typography>
          </Grid>
        </Grid>
      </Grid>
    </Grid>
);

const getFromProfile = (requests, requestId, state) => selectProfile(state, requests?.find((request) => request.id === requestId)?.recipientId);

const QuestionResults = ({question, responses, requests}) => {
  const { state } = useContext(AppContext);
  return (
    <div style={{marginBottom:"1em"}} >
      <Typography className="dark-gray-text">{question.question}</Typography>
      {
        responses?.map((response) => {
          const fromProfile = getFromProfile(requests, response.requestId, state);
          return (<QuestionResponse fromProfile={fromProfile} responseText={response.answer} />);
        })
      }
    </div>
  )
};

const FeedbackTemplateResults = ({template, requests, answers}) => (
  <div>
    <h2>{template?.title}</h2>
    {template?.questions?.map((question) => (<QuestionResults question={question} responses={answers[question.id]} requests={requests} />))}
  </div>
);

const AnnualReviewReport = ({ userId, includePeer }) => {
  const { state } = useContext(AppContext);
  const { csrf } = state;
  const [feedbackRequests, setFeedbackRequests] = useState([]);
  const [templateRequestMap, setTemplateRequestMap] = useState({});
  const [templatesMap, setTemplatesMap] = useState({});
  const [answers, setAnswers] = useState({});

  const userProfile = selectProfile(state, userId);

  useEffect(() => {
    const loadRequests = async () => {
        const res = await getFeedbackRequestsByRequestee(userId, null, csrf);
        const feedbackRequests = res.payload && res.payload.data && !res.error ? res.payload.data : [];

        setFeedbackRequests(feedbackRequests);
    }

    if(csrf && userId) {
      loadRequests();
    }
  }, [userId, csrf]);

  useEffect(() => {
    const newRequestMap = {};
    for(let i = 0; i < feedbackRequests.length; i++) {
      if(!newRequestMap[feedbackRequests[i].templateId]) {
        newRequestMap[feedbackRequests[i].templateId] = [];
      }
      newRequestMap[feedbackRequests[i].templateId].push(feedbackRequests[i]);
    }

    setTemplateRequestMap(newRequestMap);
  }, [feedbackRequests]);

  useEffect(() => {
    const refreshAnswers = async () => {
      const requests = [];
      feedbackRequests.forEach((feedbackRequest) => requests.push(getAnswersFromRequest(feedbackRequest.id)));
      const answers = await Promise.all(requests);
      setAnswers(answers.reduce((answerMap, res) => {
        const questionAnswers = res.payload && res.payload.data && !res.error ? res.payload.data : null;
        questionAnswers?.forEach((answer) => {
            answerMap[answer.questionId] = answerMap[answer.questionId] ? answerMap[answer.questionId] : [];
            answerMap[answer.questionId].push(answer);
        });

        return answerMap;
      }, {}));
    };
    if(csrf && feedbackRequests && feedbackRequests.length > 0) {
        refreshAnswers();
    }
  }, [csrf, feedbackRequests]);

  useEffect(() => {
    const refreshTemplates = async (templateIds) => {
      const requests = [];
      templateIds.forEach((templateId) => requests.push(getFeedbackTemplateWithQuestions(templateId)));
      const templates = await Promise.all(requests);
      setTemplatesMap(templates.reduce((templates, template) => {
        templates[template.id] = template;
        return templates;
      }, {}));
    }
    const templateIds = Object.keys(templateRequestMap);
    if(csrf && templateIds && templateIds.length > 0) {
        refreshTemplates(templateIds);
    }
  }, [templateRequestMap, csrf]);

  return (
    <Card id="annual-review-card">
      <CardHeader
        title={
          <Typography variant="h5" component="h2">
                {userProfile?.name}
              </Typography>
            }
            subheader={
              <Typography color="textSecondary" component="h3">
                {userProfile?.title}
          </Typography>
        }
        disableTypography
        avatar={<Avatar id="pdl-large" src={getAvatarURL(userProfile?.workEmail)} />}
      />
      <CardContent>
        <List>
          {Object.entries(templateRequestMap).map(([templateId, feedbackRequests]) => (<ListItem><FeedbackTemplateResults template={templatesMap[templateId]} requests={templateRequestMap[templateId]} answers={answers} /></ListItem>))}
        </List>
      </CardContent>
    </Card>
  );
};
export default AnnualReviewReport;
