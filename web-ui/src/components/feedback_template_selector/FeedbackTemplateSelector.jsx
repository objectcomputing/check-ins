import React, {useState} from "react";
import TemplateCard from "../template-card/TemplateCard";
import TemplatePreviewModal from "../template-preview-modal/TemplatePreviewModal";
import {useHistory, useLocation} from "react-router-dom";
import queryString from "query-string";

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

const FeedbackTemplateSelector = () => {
  const [preview, setPreview] = useState({open: false, selectedTemplate: null});
  const history = useHistory();
  const location = useLocation();
  const parsed = queryString.parse(location?.search);

  const handlePreviewOpen = (event, selectedTemplate) => {
    setPreview({open: true, selectedTemplate: selectedTemplate});
  }

  const handlePreviewClose = (selectedTemplate) => {
    setPreview({open: false, selectedTemplate: selectedTemplate});
  }

  const onCardClick = (template) => {
    if (template.isAdHoc) {
      setPreview({open: true, selectedTemplate: template});
    } else {
      console.log(`Selected ${template.title}`);
      parsed.template = template.id;
      history.push({...location, search: queryString.stringify(parsed)});
    }
  }

  return (
    <React.Fragment>
      {preview.selectedTemplate &&
      <TemplatePreviewModal
        template={preview.selectedTemplate}
        open={preview.open}
        onClose={handlePreviewClose}
      />
      }
      <div className="card-container">
        {getTemplates().map((template) => (
          <TemplateCard
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

export default FeedbackTemplateSelector;