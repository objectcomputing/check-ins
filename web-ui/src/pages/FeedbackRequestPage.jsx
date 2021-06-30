import React from "react";
import { makeStyles } from "@material-ui/core/styles";
import Stepper from "@material-ui/core/Stepper";
import Step from "@material-ui/core/Step";
import StepLabel from "@material-ui/core/StepLabel";
import Button from "@material-ui/core/Button";
import Typography from "@material-ui/core/Typography";
import { Link, useLocation, useHistory, Redirect } from 'react-router-dom';
import queryString from 'query-string';
import FeedbackTemplateSelector from "../components/feedback_template_selector/FeedbackTemplateSelector";
import FeedbackRecipientSelector from "../components/feedback_recipient_selector/FeedbackRecipientSelector";
import SelectDate from "../components/feedback_date_selector/SelectDate";
import "./FeedbackRequestPage.css";

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
  }

}));

function getSteps() {
  return ["Select template", "Select recipients", "Set dates"];
}

let todayDate = new Date();
const FeedbackRequestPage = () => {
  const steps = getSteps();
  const classes = useStyles();
  const location = useLocation();
  const history = useHistory();

  const query = queryString.parse(location?.search);

  const stepQuery = query.step?.toString();
  const templateQuery = query.template?.toString();

  let sendDate = query?.sendDate ? query.sendDate: todayDate.toString();
  let dueDate = query?.dueDate ? query.dueDate: null
  let activeStep = location?.search ? parseInt(stepQuery) : 1;
  const numbersOnly = /^\d+$/.test(stepQuery);

  const getFeedbackArgs = (step) => {
    const nextQuery = {
      ...query,
      step: step
    }

    return `/feedback/request/?${queryString.stringify(nextQuery)}`;
  }

  const handleQueryChange = (key, value) => {
    let newQuery = {
      ...query,
      [key]: value
    }
    history.push({...location, search: queryString.stringify(newQuery)});
  }
  
  if (activeStep < 1 || activeStep > steps.length || !numbersOnly) {
    return (
      <Redirect to="/feedback/request?step=1"/>
    );
  }

  return (
    <div className="feedback-request-page">
      <div className="header-container">
        <Typography className={classes.requestHeader} variant="h4">Feedback Request for <b>John Doe</b></Typography>
        <div>
            <div>
              <Link
                className={`no-underline-link ${activeStep <= 1 ? 'disabled-link' : ''}`}
                to={getFeedbackArgs(activeStep - 1)}
              >
                <Button
                  disabled={activeStep <= 1}>
                  Back
                </Button>
              </Link>

              <Link
                className={`no-underline-link ${activeStep > getSteps().length ? 'disabled-link no-underline-link' : ''}`}
                to={activeStep === 3 ? `/feedback/request/confirmation` : getFeedbackArgs(activeStep + 1)}>
                <Button
                  disabled={activeStep > getSteps().length}
                  variant="contained"
                  color="primary">
                  {activeStep === steps.length ? "Submit" : "Next"}
                </Button>
              </Link>
            </div>
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
        {activeStep === 2 && <FeedbackRecipientSelector />}
        {activeStep === 3 && <SelectDate handleQueryChange={handleQueryChange} sendDateProp = {sendDate} dueDateProp = {dueDate} />}
      </div>
    </div>
  );
}

export default FeedbackRequestPage;
