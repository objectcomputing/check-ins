import React, {useContext, useState} from "react";
import TemplateCard from "../template-card/TemplateCard";
import TemplatePreviewModal from "../template-preview-modal/TemplatePreviewModal";
import PropTypes from "prop-types";
import Button from "@material-ui/core/Button";
import {Tooltip} from "@material-ui/core";
import {createFeedbackTemplate} from "../../api/feedbacktemplate";
import {AppContext} from "../../context/AppContext";
import {selectCsrfToken, selectCurrentUser} from "../../context/selectors";

function getTemplates() {
  return [
    {
      id: 123,
      title: "Survey 1",
      isAdHoc: false,
      description: "Make a survey with a few questions",
      creator: "Admin",
      questions: []
    },
    {
      id: 124,
      title: "Feedback Survey 2",
      isAdHoc: false,
      description: "Another type of survey",
      creator: "Jane Doe",
      questions: [],
    },
    {
      id: 125,
      title: "Custom Template",
      isAdHoc: false,
      description: "A very very very very very very very very very very very very very very very very very very very very very very very very very very long description",
      creator: "Bob Smith",
      questions: []
    },
  ];
}

const propTypes = {
  query: PropTypes.string,
  changeQuery: PropTypes.func
}

const FeedbackTemplateSelector = (props) => {
  const { state } = useContext(AppContext);
  const csrf = selectCsrfToken(state);
  const currentUser = selectCurrentUser(state);
  const currentUserId = currentUser?.id;

  const [templates, setTemplates] = useState(getTemplates());
  const [preview, setPreview] = useState({open: false, selectedTemplate: null});

  const handlePreviewOpen = (event, selectedTemplate) => {
    event.stopPropagation();
    setPreview({open: true, selectedTemplate: selectedTemplate});
  }

  const handlePreviewClose = (selectedTemplate) => {
    setPreview({open: false, selectedTemplate: selectedTemplate});
  }

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
        active: false,
      };
      console.log("Creating new ad-hoc template:");
      console.log(newFeedbackTemplate);
      const res = await createFeedbackTemplate(newFeedbackTemplate, csrf);
      if (!res.error && res.payload && res.payload.data) {
        newFeedbackTemplate.id = res.payload.data.id;
        newFeedbackTemplate.isAdHoc = true;
        console.log("Response:");
        console.log(res.payload.data);
        setTemplates([...templates, newFeedbackTemplate]);
      }
    }
    setPreview({open: false, selectedTemplate: submittedTemplate});
  }

  const onCardClick = (template) => {
    if (template.isAdHoc) {
      setPreview({open: true, selectedTemplate: template});
    } else {
      props.changeQuery("template", template.id);
    }
  }

  const onNewAdHocClick = () => {
    const newAdHocTemplate = {
      title: "Ad Hoc",
      description: "Ask a single question",
      isAdHoc: true,
    }
    setPreview({open: true, selectedTemplate: newAdHocTemplate});
  }

  return (
    <React.Fragment>
      {preview.selectedTemplate &&
      <TemplatePreviewModal
        template={preview.selectedTemplate}
        open={preview.open}
        onSubmit={() => handlePreviewSubmit(preview.selectedTemplate)}
        onClose={() => handlePreviewClose(preview.selectedTemplate)}
      />
      }
      <div className="card-container">
        {templates.map((template) => (
          <TemplateCard
            key={template.id}
            title={template.title}
            creator={template.creator}
            description={template.description}
            isAdHoc={template.isAdHoc}
            questions={template.questions}
            expanded={preview.open}
            onClick={(e) => handlePreviewOpen(e, template)}
            onCardClick={() => onCardClick(template)}/>
        ))}
      </div>
      <Tooltip title="Ask a single question" arrow>
        <Button
          style={{marginLeft: "30px"}}
          variant="contained"
          color="primary"
          onClick={onNewAdHocClick}>
          New Ad-Hoc Template
        </Button>
      </Tooltip>
    </React.Fragment>
  );
}

FeedbackTemplateSelector.propTypes = propTypes;

export default FeedbackTemplateSelector;