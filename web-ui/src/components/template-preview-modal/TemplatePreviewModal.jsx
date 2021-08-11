import React, {useState, useEffect, useContext} from 'react';
import { makeStyles } from '@material-ui/core/styles';
import Dialog from '@material-ui/core/Dialog';
import AppBar from '@material-ui/core/AppBar';
import Toolbar from '@material-ui/core/Toolbar';
import IconButton from '@material-ui/core/IconButton';
import Typography from '@material-ui/core/Typography';
import CloseIcon from '@material-ui/icons/Close';
import Slide from '@material-ui/core/Slide';
import List from "@material-ui/core/List";
import ListItem from "@material-ui/core/ListItem";
import ListItemText from "@material-ui/core/ListItemText";
import Button from "@material-ui/core/Button"
import "./TemplatePreviewModal.css";
import AdHocCreationForm from "./ad_hoc_creation_form/AdHocCreationForm";
import PropTypes from "prop-types";
import {getQuestionsOnTemplate} from "../../api/feedbacktemplate";
import {AppContext} from "../../context/AppContext";
import {selectCsrfToken, selectCurrentUser, selectProfile} from "../../context/selectors";
import {ListItemAvatar, Tooltip} from "@material-ui/core";
import Avatar from "@material-ui/core/Avatar";
import {Group as GroupIcon, Person as PersonIcon} from "@material-ui/icons";

const useStyles = makeStyles((theme) => ({
  root: {
    display: 'flex',
    flexWrap: 'wrap',
  },
  labelRoot:{
    fontSize: 25,
  },
  appBar: {
    position: 'relative',
  },
  title: {
    marginLeft: theme.spacing(2),
    flex: 1,
  },
  textField: {
    marginLeft: theme.spacing(1),
    marginRight: theme.spacing(1),
    width: '25ch',
  },
  questionNumber: {
    width: "2em",
    height: "2em",
    fontSize: "1em",
    color: "white",
    backgroundColor: theme.palette.primary.main
  },
  questionListItem: {
    padding: "1.5em 1.5em"
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
}

const TemplatePreviewModal = ({ open, onSubmit, onClose, template, createAdHoc }) => {
  const classes = useStyles();
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
      getTemplateQuestions(template.id, csrf).then((questionsList) => {
        setTemplateQuestions(questionsList);
      });
    }

  }, [csrf, currentUserId, template]);

  const submitPreview = () => {
    const submittedTemplate = {...template};
    let submittedQuestion = null;
    if (createAdHoc) {
      submittedTemplate.title = newAdHocData.title;
      submittedTemplate.description = newAdHocData.description;
      submittedQuestion = newAdHocData.question;
    }
    onSubmit(submittedTemplate, submittedQuestion);
  };

  return (
    <Dialog fullScreen open={open} onClose={onClose} TransitionComponent={Transition}>
      <AppBar className={classes.appBar}>
        <Toolbar>
          <IconButton edge="start" color="inherit" onClick={onClose} aria-label="close">
            <CloseIcon />
          </IconButton>
          <Typography variant="h6" className={classes.title}>
            {createAdHoc ? "New Ad-Hoc Template" : template.title}
          </Typography>
          <Button className="ad-hoc-next-button"
                  onClick={submitPreview}
                  color="inherit">
            {template.isAdHoc && !template.id ? "Create" : "Select"}
          </Button>
        </Toolbar>
      </AppBar>

      <div className="preview-modal-content">
      {createAdHoc ?
        <AdHocCreationForm onFormChange={(form) => setNewAdHocData(form)}/> :
        <React.Fragment>
          <Typography variant="h6">{template?.description}</Typography>
          <div className="template-details-container">
            <Typography style={{color: "gray", marginRight: "1em"}}>
              <em>Created by {creatorName}</em>
            </Typography>
            {template?.isPublic
              ? <Tooltip title="This template is public. It can be used by anyone." placement="right" arrow><GroupIcon style={{color: "gray"}}/></Tooltip>
              : <Tooltip title="This template is private. It can be used by only you and admins" placement="right" arrow><PersonIcon style={{color: "gray"}}/></Tooltip>
            }
          </div>
          {templateQuestions && templateQuestions.length === 0 &&
            <Typography variant="h5" style={{marginTop: "1em"}}>
              This template has no questions
            </Typography>
          }
          <List>
            {templateQuestions && templateQuestions.map((templateQuestion) => (
              <ListItem
                className={classes.questionListItem}
                key={templateQuestion?.id}
                divider>
                <ListItemAvatar>
                  <Avatar className={classes.questionNumber}>
                    {templateQuestion?.questionNumber}
                  </Avatar>
                </ListItemAvatar>
                <ListItemText primary={templateQuestion?.question}/>
              </ListItem>
            ))}
          </List>
        </React.Fragment>
      }
      </div>
    </Dialog>
  );
}

TemplatePreviewModal.propTypes = propTypes;

export default TemplatePreviewModal;