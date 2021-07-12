import React, {useContext, useCallback} from "react";
import { makeStyles } from "@material-ui/core/styles";
import Stepper from "@material-ui/core/Stepper";
import Step from "@material-ui/core/Step";
import StepLabel from "@material-ui/core/StepLabel";
import Button from "@material-ui/core/Button";
import Typography from "@material-ui/core/Typography";
import {useLocation, useHistory } from 'react-router-dom';
import queryString from 'query-string';
import FeedbackTemplateSelector from "../components/feedback_template_selector/FeedbackTemplateSelector";
import FeedbackRecipientSelector from "../components/feedback_recipient_selector/FeedbackRecipientSelector";
import SelectDate from "../components/feedback_date_selector/SelectDate";
import "./FeedbackRequestPage.css";
import {AppContext} from "../context/AppContext";
import { createFeedbackRequest } from "../api/feedback";
import {selectProfile, selectCsrfToken, selectCurrentUser} from "../context/selectors";
import DateFnsUtils from "@date-io/date-fns";

const dateUtils = new DateFnsUtils();
const useStyles = makeStyles((theme) => ({
  root: {
    backgroundColor: "transparent",
    ['@media (max-width:767px)']: { // eslint-disable-line no-useless-computed-key
      width: '100%',
      padding: 0,
    },
  },
  stepContainer: {
    ['@media min-width(321px) and (max-width:767px)']: { // eslint-disable-line no-useless-computed-key
      width: '80%',
    },
    ['@media max-width(320px)']: { // eslint-disable-line no-useless-computed-key
      display: "none",
    },
    backgroundColor: "transparent"
  },
  appBar: {
    position: "relative",
  },
  media: {
    height: 0,
  },
  expand: {
    justifyContent: "right",
    transition: theme.transitions.create("transform", {
      duration: theme.transitions.duration.shortest,
    }),
  },
  expandOpen: {
    justifyContent: "right",
  },
  actionButtons: {
    margin: "0 0 0 1em"
  }
}));

function getSteps() {
  return ["Select template", "Select recipients", "Set dates"];
}

const FeedbackRequestPage = () => {
  const {state} = useContext(AppContext);
  const steps = getSteps();
  const classes = useStyles();
  const memberProfile = selectCurrentUser(state);
  const currentUserId = memberProfile?.id;
  const location = useLocation();
  const history = useHistory();
  const query = queryString.parse(location?.search);
  const stepQuery = query.step?.toString();
  const templateQuery = query.template?.toString();
  const fromQuery = query.from?.toString();
  const sendQuery = query.send?.toString();
  const dueQuery = query.due?.toString();
  const sendDate = query.send?.toString();
  const forQuery = query.for?.toString();
  const requestee = selectProfile(state, forQuery);
  const csrf = selectCsrfToken(state)

  const getStep = useCallback(() => {
    if (!stepQuery || stepQuery < 1 || !/^\d+$/.test(stepQuery))
      return 1;
    else return parseInt(stepQuery);
  }, [stepQuery]);

  const activeStep = getStep();

  const hasFor = useCallback(() => {
    return !!forQuery;
  }, [forQuery])

  const hasTemplate = useCallback(() => {
    return !!templateQuery;
  }, [templateQuery])

  const hasFrom = useCallback(() => {
    return !!fromQuery;
  }, [fromQuery])


  const isValidDate = useCallback((dateString) => {
    let today = new Date();
    today = dateUtils.format(today, "yyyy-MM-dd");
    let timeStamp = Date.parse(dateString)
    if(dateString < today)
      return false;
    else
      return !isNaN(timeStamp);
   }, []);

  const hasSend = useCallback(() => {
    let isValidPair = false
    if(dueQuery) {
      isValidPair = dueQuery >= sendQuery
    }
    return (sendQuery && isValidDate(sendQuery) && isValidPair)
  }, [sendQuery, isValidDate, dueQuery]);

  const canProceed = useCallback(() => {
    switch (activeStep) {
      case 1:
        return hasFor() && hasTemplate();
      case 2:
        return hasFor() && hasTemplate() && hasFrom();
      case 3:
        return hasFor() && hasTemplate() && hasFrom() && hasSend() && isValidDate(dueQuery);
      default:
        return false;
    }
  }, [activeStep, hasFor, hasTemplate, hasFrom, hasSend, dueQuery, isValidDate]);

const handleSubmit = () =>{
    let feedbackRequest = {}
    if (typeof fromQuery === 'string' && fromQuery.indexOf(',') === -1) {
        feedbackRequest = { id : null, creatorId: currentUserId, requesteeId:forQuery, recipientId: fromQuery, templateId:"6b72840f-7e18-43cc-a923-15dec8ef77f4", sendDate: sendDate, dueDate: dueQuery, status: "Pending", submitDate: null}
        sendFeedbackRequest(feedbackRequest)
    } else if (typeof fromQuery === 'string' && fromQuery.indexOf(',') !== -1) {
      let fromArray = fromQuery.split(',')
        for (const recipient of fromArray) {
           feedbackRequest = { id : null, creatorId: currentUserId, requesteeId: forQuery, recipientId: recipient, templateId: "6b72840f-7e18-43cc-a923-15dec8ef77f4", sendDate: sendDate, dueDate: dueQuery, status: "Pending", submitDate: null}
           sendFeedbackRequest(feedbackRequest)
        }
      }
    }

  const onNextClick = useCallback(() => {
    if (!canProceed()) return;
    if (activeStep === steps.length) {
      handleSubmit();
      return;
    }
    query.step = `${activeStep + 1}`;
    history.push({...location, search: queryString.stringify(query)});

  }, [canProceed, activeStep, steps.length, query, location, history]);     // eslint-disable-line react-hooks/exhaustive-deps

  const onBackClick = useCallback(() => {
    if (activeStep === 1) return;
    query.step = `${activeStep - 1}`;
    history.push({...location, search: queryString.stringify(query)});
  }, [activeStep, query, location, history]);

    const sendFeedbackRequest = async(feedbackRequest) => {
          if (csrf) {
                    let res = await createFeedbackRequest(feedbackRequest, csrf);
                    let data =
                      res.payload && res.payload.data && !res.error
                        ? res.payload.data
                        : null;
                           if (data) {
                            history.push("/feedback/request/confirmation");
                           }

              }


    }

    const urlIsValid = useCallback(() => {
      switch (activeStep) {
        case 1:
          return hasFor();
        case 2:
          return hasFor() && hasTemplate();
        case 3:
          return hasFor() && hasTemplate() && hasFrom();
        case 4:
          return hasFor() && hasTemplate() && hasFrom() && hasSend();
        default:
          return false;
      }
    }, [activeStep, hasFor, hasTemplate, hasFrom, hasSend]);

  const handleQueryChange = (key, value) => {
    let newQuery = {
      ...query,
      [key]: value
    }
    history.push({...location, search: queryString.stringify(newQuery)});
  }

  if (!urlIsValid()) {
    history.push("/checkins");
  }

  return (
    <div className="feedback-request-page">
      <div className="header-container">
        <Typography className={classes.requestHeader} variant="h4">Feedback Request for <b>{requestee?.name}</b></Typography>
        <div>
          <Button className={classes.actionButtons} onClick={onBackClick} disabled={activeStep <= 1}
                  variant="contained">
            Back
          </Button>
          <Button className={classes.actionButtons} onClick={onNextClick}
                  variant="contained" disabled={!canProceed()} color="primary">
            {activeStep === steps.length ? "Submit" : "Next"}
          </Button>
        </div>
      </div>
      <div className={classes.stepContainer}>
        <Stepper activeStep={activeStep - 1} className={classes.root}>
          {steps.map((label) => {
            const stepProps = {};
            const labelProps = {};
            return (
              <Step key={label} {...stepProps}>
                <StepLabel {...labelProps} key={label}>{label}</StepLabel>
              </Step>
            );
          })}
        </Stepper>
      </div>
      <div className="current-step-content">
        {activeStep === 1 && <FeedbackTemplateSelector changeQuery={(key, value) => handleQueryChange(key, value)} query={templateQuery}/> }
        {activeStep === 2 && <FeedbackRecipientSelector/>}
        {activeStep === 3 && <SelectDate/>}
      </div>
    </div>
  );
};

export default FeedbackRequestPage;
