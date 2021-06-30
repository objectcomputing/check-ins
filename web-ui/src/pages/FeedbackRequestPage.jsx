import React, {useCallback} from "react";
import { makeStyles } from "@material-ui/core/styles";
import Stepper from "@material-ui/core/Stepper";
import Step from "@material-ui/core/Step";
import StepLabel from "@material-ui/core/StepLabel";
import Button from "@material-ui/core/Button";
import Typography from "@material-ui/core/Typography";
import {useLocation, useHistory} from 'react-router-dom';
import queryString from 'query-string';
import FeedbackTemplateSelector from "../components/feedback_template_selector/FeedbackTemplateSelector";
import FeedbackRecipientSelector from "../components/feedback_recipient_selector/FeedbackRecipientSelector";
import SelectDate from "../components/feedback_date_selector/SelectDate";
import "./FeedbackRequestPage.css";

const useStyles = makeStyles((theme) => ({
  root: {
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
  const steps = getSteps();
  const classes = useStyles();
  const location = useLocation();
  const history = useHistory();
  const query = queryString.parse(location?.search);

  const stepQuery = query.step?.toString();
  const templateQuery = query.template?.toString();
  const fromQuery = query.from?.toString();
  const dueQuery = query.due?.toString();

  const getStep = useCallback(() => {
    if(!stepQuery || stepQuery < 1 || !/^\d+$/.test(stepQuery))
      return 1;
    else return parseInt(stepQuery);
  },[stepQuery]);

  const activeStep = getStep();

  const hasTemplate = useCallback(() => {
    return !!templateQuery;
  }, [templateQuery])

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
    switch(activeStep) {
      case 1:
        return hasTemplate();
      case 2:
        return hasTemplate() && hasFrom();
      case 3:
        return hasTemplate() && hasFrom() && hasDue();
      default:
        return false;
    }
  }, [activeStep, hasTemplate, hasFrom, hasDue]);

  const handleSubmit = () => {};

  const onNextClick = useCallback(() => {
    if(!canProceed()) return;
    if(activeStep === steps.length) handleSubmit();
    query.step = activeStep+1;
    history.push({...location, search: queryString.stringify(query)});
  },[canProceed, activeStep, steps.length, query, location, history]);

  const onBackClick = useCallback(() => {
    history.goBack();
  },[history]);

  const urlIsValid = useCallback(() => {
    switch (activeStep) {
      case 1:
        return true;
      case 2:
        return hasTemplate();
      case 3:
        return hasTemplate() && hasFrom();
      case 4:
        return hasTemplate() && hasFrom() && hasDue();
      default:
        return false;
    }
  }, [activeStep, hasTemplate, hasFrom, hasDue]);

  if (!urlIsValid()) {
    return (
        history.push("/feedback/request/")
    );
  }

  return (
    <div className="feedback-request-page">
      <div className="header-container">
        <Typography variant="h4">Feedback Request for <b>John Doe</b></Typography>
        <div>
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
      </div>
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
      <div className="current-step-content">
        {activeStep === 1 && <FeedbackTemplateSelector changeQuery={(key, value) => handleQueryChange(key, value)} query={templateQuery}/> }
        {activeStep === 2 && <FeedbackRecipientSelector />}
        {activeStep === 3 && <SelectDate />}
      </div>
    </div>
  );
};

export default FeedbackRequestPage;
