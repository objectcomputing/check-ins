import React, {useState, useCallback} from "react";
import { makeStyles } from "@material-ui/core/styles";
import Stepper from "@material-ui/core/Stepper";
import Step from "@material-ui/core/Step";
import StepLabel from "@material-ui/core/StepLabel";
import Button from "@material-ui/core/Button";
import Typography from "@material-ui/core/Typography";
import {Link, useLocation, useHistory} from 'react-router-dom';
import queryString from 'query-string';
import TemplateCard from "../components/template-card/TemplateCard"
import FeedbackRecipientSelector from "../components/feedback_recipient_selector/FeedbackRecipientSelector";
import SelectDate from "../components/feedback_date_selector/SelectDate";

import "./FeedbackRequestPage.css";
import TemplatePreviewModal from "../components/template-preview-modal/TemplatePreviewModal";

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

function getTemplates() {
  return [
    {
      id: 123,
      title: "Ad Hoc",
      isAdHoc: true,
      description: "Ask a single question.",
      creator: "Admin",
      questions: []
    },
    {
      id: 124,
      title: "Survey 1",
      isAdHoc: false,
      description: "Make a survey with a few questions",
      creator: "Admin",
      questions: []
    },
    {
      id: 125,
      title: "Feedback Survey 2",
      isAdHoc: false,
      description: "Another type of survey",
      creator: "Jane Doe",
      questions: [],
    },
    {
      id: 126,
      title: "Custom Template",
      isAdHoc: false,
      description: "A very very very very very very very very very very very very very very very very very very very very very very very very very very long description",
      creator: "Bob Smith",
      questions: []
    },
  ];
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

  const [preview, setPreview] = useState({open: false, selectedTemplate: null});

  const handlePreviewOpen = (event, selectedTemplate) => {
    event.stopPropagation();
    setPreview({open: true, selectedTemplate: selectedTemplate});
  };

  const handlePreviewClose = (selectedTemplate) => {
    setPreview({open: false, selectedTemplate: selectedTemplate});
  };

  const onCardClick = (template) => {
    history.push(`/feedback/request/?template=${template.id}`);
  }

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
      {preview.selectedTemplate &&
        <TemplatePreviewModal
          template={preview.selectedTemplate}
          open={preview.open}
          onClose={handlePreviewClose}
        />
      }

      <div className="header-container">
        <Typography variant="h4">Feedback Request for <b>John Doe</b></Typography>
        <div>
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

              <Button onClick={onNextClick}
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
          {activeStep === 1 && <TemplatePreviewModal/> &&
          <div className="card-container">
            {getTemplates().map((template) => (
              <TemplateCard
                key={`template-card-${template.id}`}
                title={template.title}
                creator={template.creator}
                description={template.description}
                isAdHoc={template.isAdHoc}
                questions={template.questions}
                selected={templateQuery && templateQuery.includes(template.id)}
                onClick={(e) => handlePreviewOpen(e, template)}
                onCardClick={() => onCardClick(template)}/>
            ))}
          </div>
        }
        {activeStep === 2 && <FeedbackRecipientSelector />}
        {activeStep === 3 && <SelectDate />}
      </div>
    </div>
  );
};

export default FeedbackRequestPage;