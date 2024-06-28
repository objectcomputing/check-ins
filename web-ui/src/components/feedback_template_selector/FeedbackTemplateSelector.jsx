import React, {
  useState,
  useContext,
  useEffect,
  useCallback,
  useRef
} from 'react';
import TemplateCard from '../template-card/TemplateCard';
import TemplatePreviewModal from '../template-preview-modal/TemplatePreviewModal';
import PropTypes from 'prop-types';
import { InputAdornment, TextField, Tooltip } from '@mui/material';
import Button from '@mui/material/Button';
import HelpOutlineIcon from '@mui/icons-material/HelpOutline';
import {
  createFeedbackTemplateWithQuestion,
  getAllFeedbackTemplates
} from '../../api/feedbacktemplate';
import { AppContext } from '../../context/AppContext';
import { selectCsrfToken, selectCurrentUser } from '../../context/selectors';
import { Search } from '@mui/icons-material';
import { UPDATE_TOAST } from '../../context/actions';

import './FeedbackTemplateSelector.css';

const propTypes = {
  query: PropTypes.string,
  changeQuery: PropTypes.func
};

const FeedbackTemplateSelector = ({ query, changeQuery }) => {
  const { state, dispatch } = useContext(AppContext);
  const csrf = selectCsrfToken(state);
  const currentUser = selectCurrentUser(state);
  const currentUserId = currentUser?.id;
  const [templates, setTemplates] = useState([]);
  const [preview, setPreview] = useState({
    open: false,
    selectedTemplate: {},
    createAdHoc: false
  });
  const [searchText, setSearchText] = useState('');
  const templatesFetched = useRef(false);

  // Gets all templates when the component mounts
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
          : null;
      if (templateList) {
        templatesFetched.current = true;
        return templateList;
      }
    }
    if (csrf && currentUserId) {
      getTemplates(csrf).then(templateList => {
        setTemplates(templateList);
      });
    }
  }, [currentUserId, csrf]);

  const handlePreviewOpen = (event, selectedTemplate) => {
    event.stopPropagation();
    setPreview({
      open: true,
      selectedTemplate: selectedTemplate,
      createAdHoc: false
    });
  };

  const handlePreviewClose = selectedTemplate => {
    setPreview({
      open: false,
      selectedTemplate: selectedTemplate,
      createAdHoc: false
    });
  };

  const handlePreviewSubmit = async (submittedTemplate, submittedQuestion) => {
    if (!currentUserId || !csrf) {
      return;
    }
    // User creates a new ad-hoc template
    if (submittedTemplate && submittedQuestion && preview.createAdHoc) {
      const newFeedbackTemplate = {
        title: submittedTemplate.title,
        description: submittedTemplate.description,
        creatorId: currentUserId,
        active: true,
        isAdHoc: true,
        isPublic: false
      };

      const newTemplateQuestion = {
        question: submittedQuestion,
        questionNumber: 1,
        inputType: 'TEXT'
      };

      const { templateRes, questionRes } =
        await createFeedbackTemplateWithQuestion(
          newFeedbackTemplate,
          newTemplateQuestion,
          csrf
        );

      if (templateRes.error || questionRes.error) {
        const errorMessage = templateRes.error
          ? 'Failed to save ad-hoc template'
          : 'Failed to save question for ad-hoc template';
        dispatch({
          type: UPDATE_TOAST,
          payload: {
            severity: 'error',
            toast: errorMessage
          }
        });
      } else if (templateRes.payload && templateRes.payload.data) {
        newFeedbackTemplate.id = templateRes.payload.data.id;
        setTemplates([...templates, newFeedbackTemplate]);
        changeQuery('template', newFeedbackTemplate.id);
      }
    } else if (submittedTemplate) {
      changeQuery('template', submittedTemplate.id);
    }

    setPreview({
      open: false,
      selectedTemplate: submittedTemplate,
      createAdHoc: false
    });
  };

  const onCardClick = useCallback(
    template => {
      if (!template || !template.id) {
        return;
      }
      if (query === template.id) {
        changeQuery('template', undefined);
      } else {
        changeQuery('template', template.id);
      }
    },
    [changeQuery, query]
  );

  const onNewAdHocClick = () => {
    setPreview({ open: true, selectedTemplate: {}, createAdHoc: true });
  };

  const getFilteredTemplates = useCallback(() => {
    if (templates === undefined) {
      return null;
    } else if (templatesFetched.current && templates.length === 0) {
      return <h2 style={{ marginLeft: '20px' }}>No templates found</h2>;
    }

    let templatesToDisplay = templates;
    if (searchText) {
      const filtered = templates.filter(
        template =>
          template.title?.toLowerCase().includes(searchText.toLowerCase()) ||
          template.description?.toLowerCase().includes(searchText.toLowerCase())
      );

      if (filtered.length === 0) {
        return <h2 style={{ marginLeft: '20px' }}>No matching templates</h2>;
      } else {
        templatesToDisplay = filtered;
      }
    }

    return templatesToDisplay.map(template => (
      <TemplateCard
        key={template.id}
        title={template.title}
        creatorId={template.creatorId}
        description={template.description}
        isAdHoc={template.isAdHoc}
        isPublic={template.isPublic}
        isSelected={query === template.id}
        questions={template.questions}
        expanded={preview.open}
        onPreviewClick={e => handlePreviewOpen(e, template)}
        onCardClick={() => onCardClick(template)}
      />
    ));
  }, [query, templates, searchText, onCardClick, preview.open]);

  return (
    <React.Fragment>
      {preview.selectedTemplate && (
        <TemplatePreviewModal
          template={preview.selectedTemplate}
          open={preview.open}
          onSubmit={(submittedTemplate, submittedQuestion) =>
            handlePreviewSubmit(submittedTemplate, submittedQuestion)
          }
          onClose={() => handlePreviewClose(preview.selectedTemplate)}
          createAdHoc={preview.createAdHoc}
        />
      )}
      <div className="feedback-template-actions">
        <TextField
          className="feedback-template-search"
          label="Search Templates..."
          placeholder="Template 1"
          value={searchText}
          onChange={e => {
            setSearchText(e.target.value);
          }}
          InputProps={{
            endAdornment: (
              <InputAdornment style={{ color: 'gray' }} position="end">
                <Search />
              </InputAdornment>
            )
          }}
        />
        <div className="ad-hoc-button">
          <Button variant="contained" color="primary" onClick={onNewAdHocClick}>
            New Ad-Hoc Template
          </Button>
          <Tooltip
            title="An ad-hoc template allows you to ask a single question"
            arrow
          >
            <HelpOutlineIcon style={{ color: 'gray', marginLeft: '10px' }} />
          </Tooltip>
        </div>
      </div>
      <div className="card-container">{getFilteredTemplates()}</div>
    </React.Fragment>
  );
};

FeedbackTemplateSelector.propTypes = propTypes;

export default FeedbackTemplateSelector;
