import React, {useState, useContext, useEffect} from "react";
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
import {AppContext} from "../context/AppContext";
import {getMember} from "../api/member";
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
  }
}));


function getSteps() {
  return ["Select template", "Select recipients", "Set due date"];
}

let todayDate = new Date();
const FeedbackRequestPage = () => {
  const { state } = useContext(AppContext);
  const { csrf} = state;
  const steps = getSteps();
  const classes = useStyles();
  const location = useLocation();
  const { search } = useLocation();
  const values = queryString.parse(search);
  const history = useHistory();
  const query = queryString.parse(location?.search);
  const stepQuery = query.step?.toString();
  const templateQuery = query.template?.toString();
  let sendDate = query?.sendDate ? query.sendDate: todayDate.toString();
  let dueDate = query?.dueDate ? query.dueDate: null
  const [requestee, setRequestee] = useState();
  const id = values.for?.toString();
  let activeStep = location?.search ? parseInt(stepQuery) : 2;


  useEffect(() => {
    async function getMemberProfile() {
      if (id) {
        let res = await getMember(id, csrf);
        let requesteeProfile =
          res.payload.data && !res.error ? res.payload.data : undefined;
        setRequestee(requesteeProfile ? requesteeProfile.name : "");
      }
    }
    if (csrf) {
      getMemberProfile();
    }
  }, [csrf, id]);

  const getFeedbackArgs = (step) => {
    const nextQuery = {
      ...query,
      for: id,
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
  
  if (activeStep < 1 || activeStep > steps.length) {
    return (
      <Redirect to="/feedback/request/?step=1"/>
    );
  }

  return (
    <div className="feedback-request-page">
      <div className="header-container">
        <Typography variant="h4">Feedback Request for <b>{requestee}</b></Typography>
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
        {activeStep === 3 && <SelectDate handleQueryChange={handleQueryChange} sendDateProp = {sendDate} dueDateProp = {dueDate} />}
      </div>
    </div>
  );
}

export default FeedbackRequestPage;
