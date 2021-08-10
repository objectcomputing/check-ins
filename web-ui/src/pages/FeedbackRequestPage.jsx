import React, {useContext, useCallback, useEffect, useState, useRef} from "react";
import { makeStyles } from "@material-ui/core/styles";
import Stepper from "@material-ui/core/Stepper";
import Step from "@material-ui/core/Step";
import StepLabel from "@material-ui/core/StepLabel";
import Button from "@material-ui/core/Button";
import Typography from "@material-ui/core/Typography";
import { useLocation, useHistory } from 'react-router-dom';
import { UPDATE_TOAST } from "../context/actions";
import queryString from 'query-string';
import FeedbackTemplateSelector from "../components/feedback_template_selector/FeedbackTemplateSelector";
import FeedbackRecipientSelector from "../components/feedback_recipient_selector/FeedbackRecipientSelector";
import SelectDate from "../components/feedback_date_selector/SelectDate";
import "./FeedbackRequestPage.css";
import {AppContext} from "../context/AppContext";
import { createFeedbackRequest } from "../api/feedback";
import {selectProfile, selectCsrfToken, selectCurrentUser, selectCurrentMemberIds} from "../context/selectors";
import DateFnsUtils from "@date-io/date-fns";
import {getFeedbackTemplate} from "../api/feedbacktemplate";
import {softDeleteAdHocTemplates} from "../api/feedbacktemplate";

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
  const {state, dispatch} = useContext(AppContext);
  const steps = getSteps();
  const classes = useStyles();
  const memberProfile = selectCurrentUser(state);
  const currentUserId = memberProfile?.id;
  const location = useLocation();
  const history = useHistory();
  const csrf = selectCsrfToken(state);
  const [query, setQuery] = useState({});
  const queryLoaded = useRef(false);
  const [readyToProceed, setReadyToProceed] = useState(false);
  const [templateIsValid, setTemplateIsValid] = useState();
  const [requestee, setRequestee] = useState({});
  const [memberIds, setMemberIds] = useState([]);
  const [activeStep, setActiveStep] = useState(1);

  const handleQueryChange = useCallback((key, value) => {
    let newQuery = {
      ...query,
      [key]: value
    }
    history.push({ ...location, search: queryString.stringify(newQuery) });
  }, [history, location, query]);

  const getStep = useCallback(() => {
    if (!query.step || query.step < 1 || !/^\d+$/.test(query.step))
      return 1;
    else return parseInt(query.step);
  }, [query.step]);

  const hasFor = useCallback(() => {
    if (!memberIds.length) return true;
    return !!query.for && memberIds.includes(query.for);
  }, [query.for, memberIds]);

  const hasFrom = useCallback(() => {
    if (!memberIds.length) return true;
    let from = query.from;
    if (from) {
      from = Array.isArray(from) ? from : [from];
      for (let recipientId of from) {
        if (!memberIds.includes(recipientId)) {
          dispatch({
            type: UPDATE_TOAST,
            payload: {
              severity: "error",
              toast: "Member ID in URL is invalid",
            },
          });
          handleQueryChange("from", undefined);
          return false;
        }
          if(recipientId === currentUserId) {
            dispatch({
              type: UPDATE_TOAST,
              payload: {
                severity: "error",
                toast: "Cannot send feedback request to the requestee",
              },
            });
            //handleQueryChange("from", undefined);
            from.splice(from.indexOf(currentUserId), 1);
            return false;
          }
        }
      return true;
    }
      return false;
  }, [memberIds, query, dispatch, handleQueryChange, currentUserId]);

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
    const isValidPair = query.due ? query.due >= query.send : true;
    return (query.send && isValidDate(query.send) && isValidPair)
  }, [query.send, isValidDate, query.due]);

  const canProceed = useCallback(() => {
    if (query && Object.keys(query).length > 0) {
      if (activeStep === 1) {
        return hasFor() && templateIsValid;
      } else if (activeStep === 2) {
        return hasFor() && templateIsValid && hasFrom();
      } else if (activeStep === 3) {
        const dueQueryValid = query.due ? isValidDate(query.due) : true;
        return hasFor() && templateIsValid && hasFrom() && hasSend() && dueQueryValid;
      } else {
        return false;
      }
    }
    return false;
  }, [hasFor, hasFrom, hasSend, isValidDate, query, templateIsValid, activeStep]);

  const sendFeedbackRequest = async (feedbackRequest) => {
    if (csrf) {
      let res = await createFeedbackRequest(feedbackRequest, csrf);
      let data =
        res.payload && res.payload.data && !res.error
          ? res.payload.data
          : null;
      if (data) {
        // If the request was successful, set created ad-hoc templates to inactive
        await softDeleteAdHoc(currentUserId);
        const newLocation = {
          pathname: "/feedback/request/confirmation",
          search: queryString.stringify(query),
        }
        history.push(newLocation)
      } else if (res.error || data === null) {
        dispatch({
          type: UPDATE_TOAST,
          payload: {
            severity: "error",
            toast: "An error has occurred while submitting your request.",
          },
        });
      }
    }
  }

  const handleSubmit = () => {
    const from = query.from ? (Array.isArray(query.from) ? query.from : [query.from]) : [];
    for (const recipient of from) {
       const feedbackRequest = {
         id: null,
         creatorId: currentUserId,
         requesteeId: query.for,
         recipientId: recipient,
         templateId: query.template,
         sendDate: query.send,
         dueDate: query.due,
         status: "pending",
         submitDate: null
       };
       sendFeedbackRequest(feedbackRequest);
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

  }, [canProceed, steps.length, query, location, history]);     // eslint-disable-line react-hooks/exhaustive-deps

  const onBackClick = useCallback(() => {
    if (activeStep === 1) return;
    query.step = `${activeStep - 1}`;
    history.push({ ...location, search: queryString.stringify(query) });
  }, [query, location, history, activeStep]);

  const softDeleteAdHoc = useCallback(async (creatorId) => {
    if (csrf) {
      await softDeleteAdHocTemplates(creatorId, csrf);
    }
  }, [csrf]);

  const urlIsValid = useCallback(() => {
    if (query) {
      if (activeStep === 1) {
        return hasFor();
      } else if (activeStep === 2) {
        return hasFor() && templateIsValid;
      } else if (activeStep === 3) {
        return hasFor() && templateIsValid && hasFrom();
      } else {
        return false;
      }
    }
    return false;
  }, [hasFor, hasFrom, query, templateIsValid, activeStep]);

  useEffect(() => {
    setActiveStep(getStep());
  }, [query.step, getStep]);

  useEffect(() => {
    const members = selectCurrentMemberIds(state);
    if (members) {
      setMemberIds(members);
    }
  }, [state]);

  useEffect(() => {
    const params = queryString.parse(location?.search);
    setQuery(params);
  }, [location.search]);

  useEffect(() => {
    async function isTemplateValid() {
      if (!query.template || !csrf) {
        return false;
      }
      let res = await getFeedbackTemplate(query.template, csrf);
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
            toast: "The ID for the template you selected does not exist.",
          },
        });
        return false;
      }
      else {
        return true;
      }
    }

    if (queryLoaded.current && csrf) {
      isTemplateValid().then((isValid) => {
        setTemplateIsValid(isValid);
      });
    } else {
      queryLoaded.current = true;
    }
  }, [csrf, query, queryLoaded]);

  useEffect(() => {
    if (!queryLoaded.current || templateIsValid === undefined) return;

    if (query.for) {
      setRequestee(selectProfile(state, query.for));
    }
    if (!urlIsValid()) {
      dispatch({
        type: UPDATE_TOAST,
        payload: {
          severity: "error",
          toast: "An error has occurred with the URL",
        },
      });
      softDeleteAdHoc(currentUserId).then(() => {
        history.push("/checkins");
      });
    }
  }, [history, state, query, currentUserId, dispatch, softDeleteAdHoc, urlIsValid, templateIsValid]);

  useEffect(() => {
    setReadyToProceed(canProceed());
  }, [canProceed]);

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
        {activeStep === 1 && <FeedbackTemplateSelector changeQuery={(key, value) => handleQueryChange(key, value)} query={query.template} />}
        {activeStep === 2 && <FeedbackRecipientSelector changeQuery={(key, value) => handleQueryChange(key, value)} fromQuery={query.from ? (Array.isArray(query.from) ? query.from : [query.from]) : []} />}
        {activeStep === 3 && <SelectDate changeQuery={(key, value) => handleQueryChange(key, value)} sendDateQuery={query.send} dueDateQuery={query.due}/>}
      </div>
    </div>
  );
}

export default FeedbackRequestPage;
