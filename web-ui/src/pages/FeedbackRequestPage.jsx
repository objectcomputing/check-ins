import React, {useEffect, useState} from "react";
import { makeStyles } from "@material-ui/core/styles";
import Stepper from "@material-ui/core/Stepper";
import Step from "@material-ui/core/Step";
import StepLabel from "@material-ui/core/StepLabel";
import Button from "@material-ui/core/Button";
import Typography from "@material-ui/core/Typography";
import { Link, useLocation, useHistory, Redirect } from 'react-router-dom';
import queryString from 'query-string';
import TemplateCard from "../components/template-card/TemplateCard"
import FeedbackRecipientSelector from "../components/feedback_recipient_selector/FeedbackRecipientSelector";
import SelectDate from "../components/feedback_date_selector/SelectDate";
import TemplatePreviewModal from "../components/template-preview-modal/TemplatePreviewModal";

import "./FeedbackRequestPage.css";
import TemplatePreviewModal from "../components/template-preview-modal/TemplatePreviewModal";
import {TextField} from "@material-ui/core";


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
      title: "Ad Hoc",
      isAdHoc: true,
      description: "Ask a single question.",
      creator: "Admin",
      questions: []
    },
    {
      title: "Survey 1",
      isAdHoc: false,
      description: "Make a survey with a few questions",
      creator: "Admin",
      questions: []
    },
    {
      title: "Feedback Survey 2",
      isAdHoc: false,
      description: "Another type of survey",
      creator: "Jane Doe",
      questions: [],
    },
    {
      title: "Custom Template",
      isAdHoc: false,
      description: "A very very very very very very very very very very very very very very very very very very very very very very very very very very long description",
      creator: "Bob Smith",
      questions: []
    },
  ];

}

let todayDate = new Date()
const FeedbackRequestPage = () => {
  const allTemplates = getTemplates();
  const steps = getSteps();
  const classes = useStyles();
  const location = useLocation();
  const history = useHistory();

  const query = queryString.parse(location?.search);

  const stepQuery = query.step?.toString();

  let sendDate = query?.sendDate ? query.sendDate: todayDate.toString();
  let dueDate = query?.dueDate ? query.dueDate: null
  let activeStep = location?.search ? parseInt(stepQuery) : 1;
  const numbersOnly = /^\d+$/.test(stepQuery);

  const [preview, setPreview] = useState({open: false, selectedTemplate: null});
  const [searchText, setSearchText] = useState("");
  const [filteredTemplates, setFilteredTemplates] = useState(allTemplates);



  useEffect(() => {

    getFilteredTemplates();

  }, [searchText])

  const handlePreviewOpen = (event, selectedTemplate) => {
    setPreview({open: true, selectedTemplate: selectedTemplate});
  }

  const handlePreviewClose = (selectedTemplate) => {
    setPreview({open: false, selectedTemplate: selectedTemplate});
  }

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

  const onCardClick = (template) => {
    if (template.isAdHoc) {
      setPreview({open: true, selectedTemplate: template});
    } else {
      console.log(`Selected ${template.title}`);
    }
  }

  if (activeStep < 1 || activeStep > steps.length || !numbersOnly) {
    return (
        <Redirect to="/feedback/request?step=1"/>
    );
  }
  const getFilteredTemplates = () => {
    console.log(searchText)
    setFilteredTemplates(setFilteredTemplates => []);
    if (searchText !== "") {
      for (const template of allTemplates) {
        let searchTextString = searchText.toString().toLowerCase();
        let title = template.title.toLowerCase();
        let description = template.description.toLowerCase();
        if (title.includes(searchTextString) || description.includes(searchTextString)) {
          setFilteredTemplates(setFilteredTemplates => [...setFilteredTemplates, template]);
        }
      }

      if(filteredTemplates.length === 0){

      }
      else{
        return filteredTemplates;
      }
    } else {
      setFilteredTemplates(setFilteredTemplates => [...allTemplates]);
    }


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

              <Link
                  className={`no-underline-link ${activeStep > getSteps().length ? 'disabled-link no-underline-link' : ''}`}
                  to={activeStep === 3 ? `/feedback/request/confirmation` : `?step=${activeStep + 1}`}>
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
          {activeStep === 1 && <TemplatePreviewModal/> &&
          <div className="search-bar">
            <TextField
                label="Search Templates..."
                placeholder="Template 1"
                value={searchText}
                onChange={(e) => {
                  setSearchText(e.target.value);
                }}
            />
            <div className="card-container">
              {
                (filteredTemplates.length === 0)
                    ? <h2>No matching templates</h2>
                    : filteredTemplates.map((template) => (
                      <TemplateCard
                      title={template.title}
                      creator={template.creator}
                      description={template.description}
                      isAdHoc={template.isAdHoc}
                      questions={template.questions}
                      expanded={preview.open}
                      onClick={(e) => handlePreviewOpen(e, template)}
                      onCardClick={() => onCardClick(template)}/>
                ))
              }
            </div>
          </div>
          }
          {activeStep === 2 && <FeedbackRecipientSelector/>}
          {activeStep === 3 && <SelectDate/>}
        </div>

      </div>
  );
}

export default FeedbackRequestPage;