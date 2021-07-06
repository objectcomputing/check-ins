import React, {useState, useContext, useEffect, useCallback} from "react";
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
import TemplatePreviewModal from "../components/template-preview-modal/TemplatePreviewModal";
import "./FeedbackRequestPage.css";
import {AppContext} from "../context/AppContext";
import { createFeedbackRequest } from "../api/feedback";
import {getMember} from "../api/member";
import {selectCurrentUser} from "../context/selectors"
import FeedbackTemplateSelector from "../components/feedback_template_selector/FeedbackTemplateSelector";
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
  const {state} = useContext(AppContext);
  const {csrf} = state;
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
  const dueQuery = query.due?.toString();
  const sendDate = query.send?.toString();
  const [requestee, setRequestee] = useState();
  const id = query.for?.toString();

  const getStep = useCallback(() => {
    if (!stepQuery || stepQuery < 1 || !/^\d+$/.test(stepQuery))
      return 1;
    else return parseInt(stepQuery);
  }, [stepQuery]);

  const activeStep = getStep();

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

    const [preview, setPreview] = useState({open: false, selectedTemplate: null});

    const handlePreviewClose = (selectedTemplate) => {
      setPreview({open: false, selectedTemplate: selectedTemplate});
    };

    const hasFor = useCallback(() => {
      return !id;
    }, [id])

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
        return hasFor() && hasTemplate();
      case 2:
        return hasFor() && hasTemplate() && hasFrom();
      case 3:
        return hasFor() && hasTemplate() && hasFrom() && hasDue();
      default:
        return false;
    }
  }, [activeStep, hasFor, hasTemplate, hasFrom, hasDue]);

    if (activeStep < 1 || activeStep > steps.length) {

    }

    const sendFeedbackRequest = async(feedbackRequest) => {
          if (csrf) {
                    let res = await createFeedbackRequest(feedbackRequest, csrf);
                    let data =
                      res.payload && res.payload.data && !res.error
                        ? res.payload.data
                        : null;
                    if (data) {
                     console.log("Data returned by create feedback req : " + data)
                    }
              }

    }

    const handleSubmit = () =>{
    let feedbackRequest = {}
    if (typeof fromQuery === 'string') {
        feedbackRequest = { id : null, creatorId: currentUserId, requesteeId: requestee, recipientId: fromQuery, templateId: templateQuery, sendDate: sendDate, dueDate: dueQuery, status: "Pending", submitDate: null}
        sendFeedbackRequest(feedbackRequest)
    } else if (Array.isArray(fromQuery)) {
        for (const recipient of fromQuery) {
           feedbackRequest = { id : null, creatorId: currentUserId, requesteeId: requestee, recipientId: recipient, templateId: templateQuery, sendDate: sendDate, dueDate: dueQuery, status: "Pending", submitDate: null}
           sendFeedbackRequest(feedbackRequest)
        }

    }

    };

    const onNextClick = useCallback(() => {
      if (!canProceed()) return;
      if (activeStep === steps.length) handleSubmit();
      query.step = activeStep + 1;
      history.push({...location, search: queryString.stringify(query)});
    }, [canProceed, activeStep, steps.length, query, location, history]);

    const onBackClick = useCallback(() => {
      history.goBack();
    }, [history]);

    const urlIsValid = useCallback(() => {
      switch (activeStep) {
        case 1:
          return true;
        case 2:
          return hasFor() && hasTemplate();
        case 3:
          return hasFor() && hasTemplate() && hasFrom();
        case 4:
          return hasFor() && hasTemplate() && hasFrom() && hasDue();
        default:
          return false;
      }
    }, [activeStep, hasFor, hasTemplate, hasFrom, hasDue]);

  const handleQueryChange = (key, value) => {
    let newQuery = {
      ...query,
      [key]: value
    }
    history.push({...location, search: queryString.stringify(newQuery)});
  }

    if (!urlIsValid()) {
      return (
        history.push("/feedback/request/")
      );
    }

  if (!urlIsValid()) {
    return (
        history.push("/feedback/request/")
    );
  }

  return (
    <div className="feedback-request-page">
          {preview.selectedTemplate &&
            <TemplatePreviewModal
              template={preview.selectedTemplate}
              open={preview.open}
              onClose={handlePreviewClose}
            />
          }
      <div className="header-container">
        <Typography variant="h4">Feedback Request for <b>{requestee}</b></Typography>
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
          {activeStep === 2 && <FeedbackRecipientSelector/>}
          {activeStep === 3 && <SelectDate/>}
        </div>
    </div>
  );
};
  export default FeedbackRequestPage;
