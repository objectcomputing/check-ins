import React from "react";
import { makeStyles } from "@material-ui/core/styles";
import Stepper from "@material-ui/core/Stepper";
import Step from "@material-ui/core/Step";
import StepLabel from "@material-ui/core/StepLabel";
import Button from "@material-ui/core/Button";
import Typography from "@material-ui/core/Typography";
import { Link, useLocation, Redirect } from 'react-router-dom';
import queryString from 'query-string';
import FeedbackRecipientSelector from "../components/feedback_recipient_selector/FeedbackRecipientSelector";

import "./FeedbackRequestPage.css";

const useStyles = makeStyles({
  root: {
    background: "transparent"
  },
});

function getSteps() {
  return ["Select template", "Select recipients", "Set due date", "Done!"];
}

const FeedbackRequestPage = () => {

  const steps = getSteps();
  const classes = useStyles();
  const urlStep = useLocation();
  const query = queryString.parse(urlStep?.search).step?.toString();
  let activeStep = urlStep?.search ? parseInt(query) : 1;
  const numbersOnly = /^\d+$/.test(query);
  if (activeStep < 1 || activeStep > getSteps().length || !numbersOnly) {
    return (
      <Redirect to="/feedback/request?step=1"/>
    );
  }

  return (
    <div className="feedback-request-page">
      <div className="header-container">
        <Typography variant="h4">Feedback Request for <b>John Doe</b></Typography>
        <div>
          {activeStep === steps.length ? (
            <div>
              <Typography>
                All steps completed!
              </Typography>
            </div>
          ) : (
            <div>
              <Link
                className={`no-underline-link ${activeStep <= 1 ? 'disabled-link' : ''}`}
                to={`?step=${activeStep - 1}`}
              >
                <Button
                  disabled={activeStep <= 1}>
                  Back
                </Button>
              </Link>

              <Link
                className={`no-underline-link ${activeStep > getSteps().length ? 'disabled-link no-underline-link' : ''}`}
                to={`?step=${activeStep + 1}`}>
                <Button
                  disabled={activeStep > getSteps().length}
                  variant="contained"
                  color="primary">
                  {activeStep === steps.length ? "Submit" : "Next"}
                </Button>
              </Link>
            </div>
          )}
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
      <div className="current-urlStep-content">
        {activeStep === 2 && <FeedbackRecipientSelector/>}
      </div>
    </div>
  );
}

export default FeedbackRequestPage;