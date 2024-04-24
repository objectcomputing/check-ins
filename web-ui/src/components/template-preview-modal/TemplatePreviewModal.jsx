import React, { useState, useEffect, useContext } from 'react';
import { styled } from '@mui/material/styles';
import Dialog from '@mui/material/Dialog';
import AppBar from '@mui/material/AppBar';
import Toolbar from '@mui/material/Toolbar';
import IconButton from '@mui/material/IconButton';
import Typography from '@mui/material/Typography';
import CloseIcon from '@mui/icons-material/Close';
import Slide from '@mui/material/Slide';
import List from '@mui/material/List';
import ListItem from '@mui/material/ListItem';
import ListItemText from '@mui/material/ListItemText';
import Button from '@mui/material/Button';
import './TemplatePreviewModal.css';
import AdHocCreationForm from './ad_hoc_creation_form/AdHocCreationForm';
import PropTypes from 'prop-types';
import { getQuestionsOnTemplate } from '../../api/feedbacktemplate';
import { AppContext } from '../../context/AppContext';
import {
  selectCsrfToken,
  selectCurrentUser,
  selectProfile
} from '../../context/selectors';
import { ListItemAvatar, Tooltip } from '@mui/material';
import Avatar from '@mui/material/Avatar';
import { Group as GroupIcon, Person as PersonIcon } from '@mui/icons-material';

const PREFIX = 'TemplatePreviewModal';
const classes = {
  root: `${PREFIX}-root`,
  labelRoot: `${PREFIX}-labelRoot`,
  appBar: `${PREFIX}-appBar`,
  title: `${PREFIX}-title`,
  textField: `${PREFIX}-textField`,
  questionNumber: `${PREFIX}-questionNumber`,
  questionListItem: `${PREFIX}-questionListItem`
};

const StyledDialog = styled(Dialog)(({ theme }) => ({
  [`& .${classes.appBar}`]: {
    position: 'relative'
  },
  [`& .${classes.title}`]: {
    marginLeft: theme.spacing(2),
    flex: 1
  },
  [`& .${classes.questionNumber}`]: {
    width: '2em',
    height: '2em',
    fontSize: '1em',
    color: 'white',
    backgroundColor: theme.palette.primary.main
  },
  [`& .${classes.questionListItem}`]: {
    padding: '1.5em 1.5em'
  }
}));

const Transition = React.forwardRef(function Transition(props, ref) {
  return <Slide direction="up" ref={ref} {...props} />;
});

const propTypes = {
  open: PropTypes.bool.isRequired,
  onSubmit: PropTypes.func,
  onClose: PropTypes.func,
  template: PropTypes.object,
  createAdHoc: PropTypes.bool
};

const TemplatePreviewModal = ({
  open,
  onSubmit,
  onClose,
  template,
  createAdHoc
}) => {
  const { state } = useContext(AppContext);
  const csrf = selectCsrfToken(state);
  const currentUserId = selectCurrentUser(state)?.id;

  const [newAdHocData, setNewAdHocData] = useState({});
  const creatorName = selectProfile(state, template?.creatorId)?.name;
  const [templateQuestions, setTemplateQuestions] = useState([]);

  useEffect(() => {
    async function getTemplateQuestions(templateId, csrf) {
      if (!currentUserId || !csrf) {
        return [];
      }
      let res = await getQuestionsOnTemplate(templateId, csrf);
      let questionList =
        res.payload &&
        res.payload.data &&
        res.payload.status === 200 &&
        !res.error
          ? res.payload.data
          : null;
      if (questionList) {
        return questionList;
      }
    }
    if (template && template.id) {
      getTemplateQuestions(template.id, csrf).then(questionsList => {
        setTemplateQuestions(questionsList);
      });
    }
  }, [csrf, currentUserId, template]);

  const submitPreview = () => {
    const submittedTemplate = { ...template };
    let submittedQuestion = null;
    if (createAdHoc) {
      submittedTemplate.title = newAdHocData.title;
      submittedTemplate.description = newAdHocData.description;
      submittedQuestion = newAdHocData.question;
    }
    onSubmit(submittedTemplate, submittedQuestion);
  };

  return (
    <StyledDialog
      className={classes.root}
      fullScreen
      open={open}
      onClose={onClose}
      TransitionComponent={Transition}
    >
      <AppBar className={classes.appBar}>
        <Toolbar>
          <IconButton
            edge="start"
            color="inherit"
            onClick={onClose}
            aria-label="close"
            size="large"
          >
            <CloseIcon />
          </IconButton>
          <Typography variant="h6" className={classes.title}>
            {createAdHoc ? 'New Ad-Hoc Template' : template.title}
          </Typography>
          <Button
            className="ad-hoc-next-button"
            onClick={submitPreview}
            disabled={!newAdHocData.title || !newAdHocData.question}
            color="inherit"
          >
            {template.isAdHoc && !template.id ? 'Create' : 'Select'}
          </Button>
        </Toolbar>
      </AppBar>

      <div className="preview-modal-content">
        {createAdHoc ? (
          <AdHocCreationForm onFormChange={form => setNewAdHocData(form)} />
        ) : (
          <React.Fragment>
            <Typography variant="h6">{template?.description}</Typography>
            <div className="template-details-container">
              <Typography style={{ color: 'gray', marginRight: '1em' }}>
                <em>Created by {creatorName}</em>
              </Typography>
              {template?.isPublic ? (
                <Tooltip
                  title="This template is public. It can be used by anyone."
                  placement="right"
                  arrow
                >
                  <GroupIcon style={{ color: 'gray' }} />
                </Tooltip>
              ) : (
                <Tooltip
                  title="This template is private. It can be used by only you and admins"
                  placement="right"
                  arrow
                >
                  <PersonIcon style={{ color: 'gray' }} />
                </Tooltip>
              )}
            </div>
            {templateQuestions && templateQuestions.length === 0 && (
              <Typography variant="h5" style={{ marginTop: '1em' }}>
                This template has no questions
              </Typography>
            )}
            <List>
              {templateQuestions &&
                templateQuestions.map(templateQuestion => (
                  <ListItem
                    className={classes.questionListItem}
                    key={templateQuestion?.id}
                    divider
                    secondaryAction={
                      <Typography
                        variant="body1"
                        fontStyle="italic"
                        color="gray"
                      >
                        {templateQuestion?.inputType}
                      </Typography>
                    }
                  >
                    <ListItemAvatar>
                      <Avatar className={classes.questionNumber}>
                        {templateQuestion?.questionNumber}
                      </Avatar>
                    </ListItemAvatar>
                    <ListItemText primary={templateQuestion?.question} />
                  </ListItem>
                ))}
            </List>
          </React.Fragment>
        )}
      </div>
    </StyledDialog>
  );
};

TemplatePreviewModal.propTypes = propTypes;

export default TemplatePreviewModal;
