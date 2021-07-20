import React, {useContext, useCallback, useEffect, useState} from "react";
import { makeStyles } from "@material-ui/core/styles";
import Stepper from "@material-ui/core/Stepper";
import Step from "@material-ui/core/Step";
import StepLabel from "@material-ui/core/StepLabel";
import Button from "@material-ui/core/Button";
import Typography from "@material-ui/core/Typography";
import { useLocation, useHistory } from 'react-router-dom';
import queryString from 'query-string';
import FeedbackTemplateSelector from "../components/feedback_template_selector/FeedbackTemplateSelector";
import FeedbackRecipientSelector from "../components/feedback_recipient_selector/FeedbackRecipientSelector";
import SelectDate from "../components/feedback_date_selector/SelectDate";
import "./FeedbackRequestPage.css";
import {AppContext} from "../context/AppContext";
import { createFeedbackRequest } from "../api/feedback";
import {selectProfile, selectCsrfToken, selectCurrentUser, selectCurrentMembers, selectCurrentMemberIds} from "../context/selectors";
import DateFnsUtils from "@date-io/date-fns";
import {getFeedbackTemplate} from "../api/feedbacktemplate";
import { UPDATE_TOAST } from "../context/actions";

const dateUtils = new DateFnsUtils();
const useStyles = makeStyles((theme) => ({
  root: {
    backgroundColor: "transparent",
    ['@media (max-width:767px)']: { // eslint-disable-line no-useless-computed-key
      width: '100%',
      padding: 0,
    },
  },
  requestHeader: {
    ['@media (max-width:820px)']: { // eslint-disable-line no-useless-computed-key
      fontSize: "x-large",
      marginBottom: "1em",
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
    margin: "0.5em 0 0 1em",
    ['@media (max-width:820px)']: { // eslint-disable-line no-useless-computed-key
      padding: "0",
    },
  }
}));

function getSteps() {
  return ["Select template", "Select recipients", "Set dates"];
}

const FeedbackRequestPage = () => {
  const { state } = useContext(AppContext);
  const steps = getSteps();
  const classes = useStyles();
  const memberProfile = selectCurrentUser(state);
  const currentUserId = memberProfile?.id;
  const location = useLocation();
  const history = useHistory();


  const [query, setQuery] = useState(null);
  useEffect(()=> {
    setQuery(queryString.parse(location?.search));
  }, [queryString, location.search])
  const stepQuery = query?.step?.toString();
  const templateQuery = query?.template?.toString();
  const fromQuery = query?.from?.toString();
  const sendQuery = query?.send?.toString();
  const dueQuery = query?.due?.toString();
  const sendDate = query?.send?.toString();
  const forQuery = query?.for?.toString();
  const requestee = selectProfile(state, forQuery);
  const memberIds = selectCurrentMemberIds(state);
  const csrf = selectCsrfToken(state)
  const [readyToProceed, setReadyToProceed] = useState(false);
  const pathname = location.pathname;

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
      async function isTemplateValid() {
        if (!templateQuery || !csrf) {
          return false;
        }
        let res = await getFeedbackTemplate(templateQuery, csrf);
        let templateResponse =
            res.payload &&
            res.payload.data &&
            res.payload.status === 200 &&
            !res.error
                ? res.payload.data
                : null
        if (templateResponse === null) {
          window.snackDispatch({
            type: UPDATE_TOAST,
            payload: {
              severity: "error",
              toast: "The Id for the template you selected does not exist.",
            },
          });
          return false;
        }
        else{
          return true
        }
      }
    if (csrf && templateQuery) {
        isTemplateValid().then((isValid) => {
          return isValid;
        })
      }
     return !!templateQuery;
    }, [csrf, templateQuery]);


  const hasFrom = useCallback(() => {
    if (fromQuery) {
      const recipientList = fromQuery.split(",");
      for (let recipientId of recipientList) {
        if (!memberIds.includes(recipientId)) {
          query.from = undefined;
          history.push({pathname, search: queryString.stringify(query)});
          return false;
        }
      }
      return true;
    }
    return false;
  }, [fromQuery, memberIds, history, pathname, query])


  const isValidDate = useCallback((dateString) => {
    let today = new Date();
    today = dateUtils.format(today, "yyyy-MM-dd");
    let timeStamp = Date.parse(dateString)
    if (dateString < today)
      return false;
    else
      return !isNaN(timeStamp);
  }, []);

  const hasSend = useCallback(() => {
    const isValidPair = dueQuery ? dueQuery >= sendQuery : true;
    return (sendQuery && isValidDate(sendQuery) && isValidPair)
  }, [sendQuery, isValidDate, dueQuery]);

  const canProceed = useCallback(() => {
    if(query) {
      switch (activeStep) {
        case 1:
          return hasFor() && hasTemplate();
        case 2:
          return hasFor() && hasTemplate() && hasFrom();
        case 3:
          const dueQueryValid = dueQuery ? isValidDate(dueQuery) : true;
          return hasFor() && hasTemplate() && hasFrom() && hasSend() && dueQueryValid;
        default:
          return false;
      }
    }
    return false;
  }, [activeStep, hasFor, hasTemplate, hasFrom, hasSend, dueQuery, isValidDate, query]);

const handleSubmit = () =>{
    let feedbackRequest = {}
    let fromArray = fromQuery.split(',')
    if (fromArray.length === 1 ) {
        feedbackRequest = { id : null, creatorId: currentUserId, requesteeId:forQuery, recipientId: fromQuery, templateId:"6b72840f-7e18-43cc-a923-15dec8ef77f4", sendDate: sendDate, dueDate: dueQuery, status: "Pending", submitDate: null}
        sendFeedbackRequest(feedbackRequest)
    } else if (fromArray.length > 1) {
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
    history.push({ ...location, search: queryString.stringify(query) });
  }, [activeStep, query, location, history]);

    const sendFeedbackRequest = async(feedbackRequest) => {
          if (csrf) {
                    let res = await createFeedbackRequest(feedbackRequest, csrf);
                    let data =
                      res.payload && res.payload.data && !res.error
                        ? res.payload.data
                        : null;
                           if (data) {
                            const newLocation = {
                              pathname: "/feedback/request/confirmation",
                              search: queryString.stringify(query),
                            }
                            history.push(newLocation)
                           }

              }


    }

    const urlIsValid = useCallback(() => {
      if(query) {
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
      }
      return true;
    }, [activeStep, hasFor, hasTemplate, hasFrom, hasSend, query]);
  
  const handleQueryChange = (key, value) => {
    let newQuery = {
      ...query,
      [key]: value
    }
    history.push({ ...location, search: queryString.stringify(newQuery) });
  }

  useEffect(()=> {
    if (!urlIsValid()) {
      history.push("/checkins");
    }
  }, [history, urlIsValid]);

  useEffect(()=> {
    setReadyToProceed(canProceed());
  }, [canProceed])

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
            variant="contained" disabled={!readyToProceed} color="primary">
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
        {activeStep === 1 && <FeedbackTemplateSelector changeQuery={(key, value) => handleQueryChange(key, value)} query={templateQuery} />}
        {activeStep === 2 && <FeedbackRecipientSelector />}
        {activeStep === 3 && <SelectDate />}
      </div>
    </div>
  );
};

export default FeedbackRequestPage;
