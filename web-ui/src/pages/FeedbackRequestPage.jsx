import React, {useContext, useCallback} from "react";
import { makeStyles } from "@material-ui/core/styles";
import Stepper from "@material-ui/core/Stepper";
import Step from "@material-ui/core/Step";
import StepLabel from "@material-ui/core/StepLabel";
import Button from "@material-ui/core/Button";
import Typography from "@material-ui/core/Typography";
import {useLocation, useHistory} from 'react-router-dom';
import queryString from 'query-string';
import FeedbackRecipientSelector from "../components/feedback_recipient_selector/FeedbackRecipientSelector";
import SelectDate from "../components/feedback_date_selector/SelectDate";
import "./FeedbackRequestPage.css";
import {AppContext} from "../context/AppContext";
import FeedbackTemplateSelector from "../components/feedback_template_selector/FeedbackTemplateSelector";
import {selectProfile} from "../context/selectors";

const useStyles = makeStyles((theme) => ({
  root: {
    backgroundColor: "transparent",
    ['@media (max-width:767px)']: { // eslint-disable-line no-useless-computed-key
      width: '100%',
      padding: 0,
    },
  },
  requestHeader: {
    marginLeft: "2%",

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
  return ["Select template", "Select recipients", "Set due date"];
}

const FeedbackRequestPage = () => {
  const {state} = useContext(AppContext);
  const steps = getSteps();
  const classes = useStyles();
  const location = useLocation();
  const history = useHistory();
  const query = queryString.parse(location?.search);
  const stepQuery = query.step?.toString();
  const templateQuery = query.template?.toString();
  const fromQuery = query.from?.toString();
  const dueQuery = query.due?.toString();
  const forQuery = query.for?.toString();
  const requestee = selectProfile(state, forQuery);

  const getStep = useCallback(() => {
    if (!stepQuery || stepQuery < 1 || !/^\d+$/.test(stepQuery))
      return 1;
    else return parseInt(stepQuery);
  }, [stepQuery]);

  const activeStep = getStep();

  const hasTemplate = useCallback(() => {
    return !!templateQuery;
  }, [templateQuery])

  const hasFor = useCallback(() => {
    return !!forQuery;
  }, [forQuery])

  const hasFrom = useCallback(() => {
    return !!fromQuery;
  }, [fromQuery])

  const isValidDate = useCallback((dateString) => {
    const timestamp = Date.parse(dateString);
    return !isNaN(timestamp);
  }, []);

  const hasDue = useCallback(() => {
    return (dueQuery && isValidDate(dueQuery))
  }, [dueQuery, isValidDate]);

  const canProceed = useCallback(() => {
    switch (activeStep) {
      case 1:
        return hasFor() && hasTemplate();
      case 2:
        return hasFor() && hasTemplate() && hasFrom();
      case 3:
        return hasFor() && hasTemplate() && hasFrom() && hasDue();
      default:
        return false;
    }
  }, [activeStep, hasTemplate, hasFrom, hasDue, hasFor]);

  const handleSubmit = () => {
    history.push("/feedback/request/confirmation");
  };

  const onNextClick = useCallback(() => {
    if (!canProceed()) return;
    if (activeStep === steps.length) {
      handleSubmit();
      return;
    }
    query.step = activeStep + 1;
    history.push({...location, search: queryString.stringify(query)});
  }, [canProceed, activeStep, steps.length, query, location, history]);

  const onBackClick = useCallback(() => {
    history.goBack();
  }, [history]);

  const urlIsValid = useCallback(() => {
    switch (activeStep) {
      case 1:
        console.log(JSON.stringify(query));
        return hasFor();
      case 2:
        console.log(JSON.stringify(query));
        return hasFor() && hasTemplate();
      case 3:
        console.log(JSON.stringify(query));
        return hasFor() && hasTemplate() && hasFrom();
      case 4:
        console.log(JSON.stringify(query));
        return hasFor() && hasTemplate() && hasFrom() && hasDue();
      default:
        console.log("None of the above, so false!");
        return false;
    }
  }, [activeStep, hasFor, hasTemplate, hasFrom, hasDue, query]);

  if (!urlIsValid()) {
      history.push("/checkins");
  }

  const handleQueryChange = (key, value) => {
    let newQuery = {
      ...query,
      [key]: value
    }
    history.push({...location, search: queryString.stringify(newQuery)});
  }
  return (
    <div className="feedback-request-page">
      <div className="header-container">
        <Typography className= {classes.requestHeader} variant="h4">Feedback Request for <b>{requestee?.name}</b></Typography>
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
      <div className= {classes.stepContainer}>
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
