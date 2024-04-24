import React, { useContext, useEffect, useState } from 'react';

import { getAvatarURL } from '../../api/api.js';
import { AppContext } from '../../context/AppContext';
import { selectProfile } from '../../context/selectors';
import { getFeedbackRequestsByRequestee } from '../../api/feedback.js';
import { getFeedbackTemplateWithQuestions } from '../../api/feedbacktemplate.js';
import { getAnswersFromRequest } from '../../api/feedbackanswer';
import DateFnsAdapter from '@date-io/date-fns';
import {
  Avatar,
  Card,
  CardHeader,
  CardContent,
  Typography,
  List,
  ListItem,
  Grid
} from '@mui/material';
import { styled } from '@mui/material/styles';

const dateFns = new DateFnsAdapter();

const PREFIX = 'FeedbackRequestSubcard';
const classes = {
  redTypography: `${PREFIX}-redTypography`,
  yellowTypography: `${PREFIX}-yellowTypography`,
  greenTypography: `${PREFIX}-greenTypography`,
  darkGrayTypography: `${PREFIX}-darkGrayTypography`
};

// TODO jss-to-styled codemod: The Fragment root was replaced by div. Change the tag if needed.
const Root = styled('div')({
  marginBottom: '1em',
  [`& .${classes.redTypography}`]: {
    color: '#FF0000',
    marginRight: '1em'
  },
  [`& .${classes.yellowTypography}`]: {
    color: '#EE8C00',
    marginRight: '1em'
  },
  [`& .${classes.greenTypography}`]: {
    color: '#006400',
    marginRight: '1em'
  },
  [`& .${classes.darkGrayTypography}`]: {
    color: '#333333',
    marginRight: '1em'
  }
});

const Submitted = ({ submitDate, dueDate }) => {
  if (dueDate) {
    let today = new Date();
    let due = new Date(dueDate);
    if (!submitDate && today > due) {
      return <Typography className={classes.redTypography}>Overdue</Typography>;
    }
  }
  if (submitDate) {
    return (
      <React.Fragment>
        <Typography className={classes.greenTypography}>Submitted</Typography>
        <Typography className={classes.greenTypography}>
          {dateFns.format(new Date(submitDate.join('/')), 'LLLL dd, yyyy')}
        </Typography>
      </React.Fragment>
    );
  } else
    return (
      <Typography className={classes.yellowTypography}>
        Not Submitted
      </Typography>
    );
};

const QuestionResponse = ({
  fromProfile,
  responseText,
  submitDate,
  dueDate
}) => (
  <Grid container spacing={0} style={{ marginBottom: '1em' }}>
    <Grid item xs={12}>
      <Grid container direction="row" alignItems="center" className="no-wrap">
        <Grid item xs={1}>
          <Avatar
            style={{ marginRight: '1em' }}
            src={getAvatarURL(fromProfile?.workEmail)}
          />
        </Grid>
        <Grid item xs={3} className="small-margin">
          <Typography className="person-name">{fromProfile?.name}</Typography>
          <Typography className="position-text">
            {fromProfile?.title}
          </Typography>
        </Grid>
        <Grid item xs={3} className="small-margin">
          <Submitted submitDate={submitDate} dueDate={dueDate} />
        </Grid>
        <Grid item xs={5}>
          <Typography className="dark-gray-text">{responseText}</Typography>
        </Grid>
      </Grid>
    </Grid>
  </Grid>
);

const findRequestById = (requests, requestId) =>
  requests?.find(request => request.id === requestId);

const QuestionResults = ({ question, responses, requests }) => {
  const { state } = useContext(AppContext);
  return (
    <Root>
      <Typography className="dark-gray-text">{question.question}</Typography>
      {responses?.map(response => {
        const request = findRequestById(requests, response.requestId);
        const fromProfile = selectProfile(state, request?.recipientId);
        return (
          <QuestionResponse
            fromProfile={fromProfile}
            responseText={response.answer}
            submitDate={request?.submitDate}
            dueDate={request?.dueDate}
          />
        );
      })}
    </Root>
  );
};

const FeedbackTemplateResults = ({
  includeUnsubmitted,
  template,
  requests,
  answers,
  state
}) => (
  <Root>
    <h2>{template?.title}</h2>
    {includeUnsubmitted &&
      requests
        .filter(request => request.status !== 'submitted')
        .map(request => {
          const fromProfile = selectProfile(state, request?.recipientId);
          const submitDate = request?.submitDate;
          const dueDate = request?.dueDate;
          return (
            <Grid
              container
              direction="row"
              alignItems="center"
              className="no-wrap"
              spacing={0}
              style={{ marginBottom: '1em' }}
            >
              <Grid item xs={1}>
                <Avatar
                  style={{ marginRight: '1em' }}
                  src={getAvatarURL(fromProfile?.workEmail)}
                />
              </Grid>
              <Grid item xs={3} className="small-margin">
                <Typography className="person-name">
                  {fromProfile?.name}
                </Typography>
                <Typography className="position-text">
                  {fromProfile?.title}
                </Typography>
              </Grid>
              <Grid item xs={3} className="small-margin">
                <Submitted submitDate={submitDate} dueDate={dueDate} />
              </Grid>
            </Grid>
          );
        })}
    {template?.questions?.map(question => (
      <QuestionResults
        question={question}
        responses={answers[question.id]}
        requests={requests}
      />
    ))}
  </Root>
);

const AnnualReviewReport = ({ userId, includeUnsubmitted }) => {
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
      let feedbackRequests =
        res.payload && res.payload.data && !res.error ? res.payload.data : [];
      if (!includeUnsubmitted) {
        feedbackRequests = feedbackRequests.filter(
          request => request.status === 'submitted'
        );
      }
      setFeedbackRequests(feedbackRequests);
    };

    if (csrf && userId) {
      loadRequests();
    }
  }, [userId, includeUnsubmitted, csrf]);

  useEffect(() => {
    const newRequestMap = {};
    for (let i = 0; i < feedbackRequests.length; i++) {
      if (!newRequestMap[feedbackRequests[i].templateId]) {
        newRequestMap[feedbackRequests[i].templateId] = [];
      }
      newRequestMap[feedbackRequests[i].templateId].push(feedbackRequests[i]);
    }

    setTemplateRequestMap(newRequestMap);
  }, [feedbackRequests]);

  useEffect(() => {
    const refreshAnswers = async () => {
      const requests = [];
      feedbackRequests.forEach(feedbackRequest =>
        requests.push(getAnswersFromRequest(feedbackRequest.id))
      );
      const answers = await Promise.all(requests);
      setAnswers(
        answers.reduce((answerMap, res) => {
          const questionAnswers =
            res.payload && res.payload.data && !res.error
              ? res.payload.data
              : null;
          questionAnswers?.forEach(answer => {
            answerMap[answer.questionId] = answerMap[answer.questionId]
              ? answerMap[answer.questionId]
              : [];
            answerMap[answer.questionId].push(answer);
          });

          return answerMap;
        }, {})
      );
    };
    if (csrf && feedbackRequests && feedbackRequests.length > 0) {
      refreshAnswers();
    }
  }, [csrf, feedbackRequests]);

  useEffect(() => {
    const refreshTemplates = async templateIds => {
      const requests = [];
      templateIds.forEach(templateId =>
        requests.push(getFeedbackTemplateWithQuestions(templateId))
      );
      const templates = await Promise.all(requests);
      setTemplatesMap(
        templates.reduce((templates, template) => {
          templates[template.id] = template;
          return templates;
        }, {})
      );
    };
    const templateIds = Object.keys(templateRequestMap);
    if (csrf && templateIds && templateIds.length > 0) {
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
        avatar={
          <Avatar id="pdl-large" src={getAvatarURL(userProfile?.workEmail)} />
        }
      />
      <CardContent>
        <List>
          {Object.entries(templateRequestMap).map(
            ([templateId, feedbackRequests]) => (
              <ListItem>
                <FeedbackTemplateResults
                  includeUnsubmitted={includeUnsubmitted}
                  template={templatesMap[templateId]}
                  requests={templateRequestMap[templateId]}
                  answers={answers}
                  state={state}
                />
              </ListItem>
            )
          )}
        </List>
      </CardContent>
    </Card>
  );
};
export default AnnualReviewReport;
