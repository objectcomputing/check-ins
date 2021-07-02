import React, {useContext, useEffect, useState} from "react";
import TemplateCard from "../template-card/TemplateCard";
import TemplatePreviewModal from "../template-preview-modal/TemplatePreviewModal";
import PropTypes from "prop-types";
import Button from "@material-ui/core/Button";
import {Tooltip} from "@material-ui/core";
import HelpOutlineIcon from "@material-ui/icons/HelpOutline";
import {
  createFeedbackTemplate,
  getAllFeedbackTemplates
} from "../../api/feedbacktemplate";
import {AppContext} from "../../context/AppContext";
import {selectCsrfToken, selectCurrentUser} from "../../context/selectors";

import "./FeedbackTemplateSelector.css";

function getFakeTemplates() {
  return [
    {
      id: 123,
      title: "Survey 1",
      isAdHoc: false,
      description: "Make a survey with a few questions",
      createdBy: "01b7d769-9fa2-43ff-95c7-f3b950a27bf9",
      questions: []
    },
    {
      id: 124,
      title: "Feedback Survey 2",
      isAdHoc: false,
      description: "Another type of survey",
      createdBy: "2559a257-ae84-4076-9ed4-3820c427beeb",
      questions: [],
    },
    {
      id: 125,
      title: "Custom Template",
      isAdHoc: false,
      description: "A very very very very very very very very very very very very very very very very very very very very very very very very very very long description",
      createdBy: "802cb1f5-a255-4236-8719-773fa53d79d9",
      questions: []
    },
  ];
}

const propTypes = {
  query: PropTypes.string,
  changeQuery: PropTypes.func
};

const FeedbackTemplateSelector = (props) => {
  const { state } = useContext(AppContext);
  const csrf = selectCsrfToken(state);
  const currentUser = selectCurrentUser(state);
  const currentUserId = currentUser?.id;

  const [templates, setTemplates] = useState([]);
  const [preview, setPreview] = useState({open: false, selectedTemplate: null});

  useEffect(() => {
    async function getTemplates(csrf) {
      if (!currentUserId || !csrf) {
        return [];
      }
      let res = await getAllFeedbackTemplates(csrf);
      let templateList =
        res.payload &&
        res.payload.data &&
        res.payload.status === 200 &&
        !res.error
          ? res.payload.data
          : null
      if (templateList) {
        return [...templateList, ...getFakeTemplates()];
      }
    }
    if (csrf && currentUserId) {
      getTemplates(csrf).then((templateList) => {
        setTemplates(templateList);
      });
    }
  }, [currentUserId, csrf]);

  const handlePreviewOpen = (event, selectedTemplate) => {
    event.stopPropagation();
    setPreview({open: true, selectedTemplate: selectedTemplate});
  };

  const handlePreviewClose = (selectedTemplate) => {
    setPreview({open: false, selectedTemplate: selectedTemplate});
  };

  const handlePreviewSubmit = async (submittedTemplate) => {
    console.log(submittedTemplate);
    if (!currentUserId || !csrf) {
      return;
    }
    if (submittedTemplate && submittedTemplate.isAdHoc) {
      let newFeedbackTemplate = {
        title: submittedTemplate.title,
        description: submittedTemplate.description,
        createdBy: currentUserId,
        active: true,
      };

      const res = await createFeedbackTemplate(newFeedbackTemplate, csrf);
      if (!res.error && res.payload && res.payload.data) {
        newFeedbackTemplate.id = res.payload.data.id;
        newFeedbackTemplate.isAdHoc = true;
        setTemplates([...templates, newFeedbackTemplate]);
        props.changeQuery("template", newFeedbackTemplate.id);
      }
    }
    setPreview({open: false, selectedTemplate: submittedTemplate});
  };

  const onCardClick = (template) => {
    if (template && template.id) {
      props.changeQuery("template", template.id);
    }
  };

  const onNewAdHocClick = () => {
    const newAdHocTemplate = {
      title: "Ad Hoc",
      description: "Ask a single question",
      createdBy: currentUserId,
      isAdHoc: true,
    }
    setPreview({open: true, selectedTemplate: newAdHocTemplate});
  };

  return (
    <React.Fragment>
      {preview.selectedTemplate &&
      <TemplatePreviewModal
        template={preview.selectedTemplate}
        open={preview.open}
        onSubmit={(submittedTemplate) => handlePreviewSubmit(submittedTemplate)}
        onClose={() => handlePreviewClose(preview.selectedTemplate)}
      />
      }
      <div className="feedback-ad-hoc-action-buttons">
        <Button
          variant="contained"
          color="primary"
          onClick={onNewAdHocClick}>
          New Ad-Hoc Template
        </Button>
        <Tooltip title="An ad-hoc template allows you to ask a single question" arrow>
          <HelpOutlineIcon style={{color: "gray", marginLeft: "10px"}}/>
        </Tooltip>
      </div>
      <div className="card-container">
        {templates.map((template) => (
          <TemplateCard
            key={template.id}
            title={template.title}
            createdBy={template.createdBy}
            description={template.description}
            isAdHoc={template.isAdHoc}
            questions={template.questions}
            expanded={preview.open}
            onPreviewClick={(e) => handlePreviewOpen(e, template)}
            onCardClick={() => onCardClick(template)}/>
        ))}
      </div>
    </React.Fragment>
  );
};

FeedbackTemplateSelector.propTypes = propTypes;

export default FeedbackTemplateSelector;