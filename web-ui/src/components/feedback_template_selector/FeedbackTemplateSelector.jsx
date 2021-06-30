import React, {useState} from "react";
import TemplateCard from "../template-card/TemplateCard";
import TemplatePreviewModal from "../template-preview-modal/TemplatePreviewModal";
import PropTypes from "prop-types";

function getTemplates() {
  return [
    {
      id: 1,
      title: "Ad Hoc",
      isAdHoc: true,
      description: "Ask a single question.",
      creator: "Admin",
      questions: []
    },
    {
      id: 2,
      title: "Survey 1",
      isAdHoc: false,
      description: "Make a survey with a few questions",
      creator: "Admin",
      questions: []
    },
    {
      id: 3,
      title: "Feedback Survey 2",
      isAdHoc: false,
      description: "Another type of survey",
      creator: "Jane Doe",
      questions: [],
    },
    {
      id: 4,
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
  const [preview, setPreview] = useState({open: false, selectedTemplate: null});

  const handlePreviewOpen = (event, selectedTemplate) => {
    setPreview({open: true, selectedTemplate: selectedTemplate});
  }

  const handlePreviewSubmit = (selectedTemplate) => {
    props.changeQuery("template", selectedTemplate.id);
    setPreview({open: false, selectedTemplate: selectedTemplate});
  }

  const handlePreviewClose = (selectedTemplate) => {
    setPreview({open: false, selectedTemplate: selectedTemplate});
  }

  const onCardClick = (template) => {
    if (template.isAdHoc) {
      setPreview({open: true, selectedTemplate: template});
    } else {
      props.changeQuery("template", template.id);
    }
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
        {getTemplates().map((template) => (
          <TemplateCard
            key = {template.id}
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
    </React.Fragment>
  );
}

FeedbackTemplateSelector.propTypes = propTypes;

export default FeedbackTemplateSelector;