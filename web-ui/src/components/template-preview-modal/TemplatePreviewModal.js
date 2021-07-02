import React, {useState} from 'react';
import { makeStyles } from '@material-ui/core/styles';
import Dialog from '@material-ui/core/Dialog';
import AppBar from '@material-ui/core/AppBar';
import Toolbar from '@material-ui/core/Toolbar';
import IconButton from '@material-ui/core/IconButton';
import Typography from '@material-ui/core/Typography';
import CloseIcon from '@material-ui/icons/Close';
import Slide from '@material-ui/core/Slide';
import {TextField} from "@material-ui/core";
import List from "@material-ui/core/List";
import ListItem from "@material-ui/core/ListItem";
import ListItemText from "@material-ui/core/ListItemText";
import Divider from "@material-ui/core/Divider";
import Button from "@material-ui/core/Button"
import "./TemplatePreviewModal.css";

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

  }
}));

const Transition = React.forwardRef(function Transition(props, ref) {
  return <Slide direction="up" ref={ref} {...props} />;
});

const TemplatePreviewModal = ({ open, onSubmit, onClose, template }) => {

  const classes = useStyles();

  const [adHocTitle, setAdHocTitle] = useState(template?.title);
  const [adHocDescription, setAdHocDescription] = useState(template?.description);

  const submitPreview = () => {
    if (!template) {
      return;
    }

    const submittedTemplate = {...template};
    if (template.isAdHoc) {
      submittedTemplate.title = adHocTitle;
      submittedTemplate.description = adHocDescription;
    }
    onSubmit(submittedTemplate);
    setAdHocTitle(template?.title);
    setAdHocDescription(template?.description);
  };

  if (!template) {
    return null;
  }

  return (
    <Dialog fullScreen open={open} onClose={onClose} TransitionComponent={Transition}>
      <AppBar className={classes.appBar}>
        <Toolbar>
          <IconButton edge="start" color="inherit" onClick={onClose} aria-label="close">
            <CloseIcon />
          </IconButton>
          <Typography variant="h6" className={classes.title}>
            {template.isAdHoc && !template.id ? "New Ad-Hoc Template" : template.title}
          </Typography>
          <Button className="ad-hoc-next-button"
                  onClick={submitPreview}
                  color="inherit">
            {template.isAdHoc && !template.id ? "Create" : "Select"}
          </Button>
        </Toolbar>
      </AppBar>

      <div className="preview-modal-content">
      {template.isAdHoc && !template.id ?
        <React.Fragment>
          <TextField
            id="standard-full-width"
            label="Title"
            placeholder="Ad Hoc"
            fullWidth
            margin="normal"
            value={adHocTitle}
            onChange={(event) => {
              setAdHocTitle(event.target.value)
            }}/>
          <TextField
            id="standard-full-width"
            label="Description"
            placeholder="Ask a single question"
            fullWidth
            margin="normal"
            value={adHocDescription}
            onChange={(event) => {
              setAdHocDescription(event.target.value)

            }}/>
        </React.Fragment>
        :
        <Typography>{template.description}</Typography>
      }

      {template.isAdHoc && !template.id ?
        <TextField
          id="standard-full-width"
          label="Ask a feedback question"
          placeholder="How is your day going?"
          fullWidth
          multiline
          rowsMax={10}
          margin="normal"/>
        :
        <List>
          {template.questions && template.questions.map((question, index) => (
            <React.Fragment>
              <ListItem button>
                <ListItemText primary={`Question ${index + 1}`} secondary={question}/>
              </ListItem>
              <Divider/>
            </React.Fragment>
          ))}
        </List>
      }
      </div>
    </Dialog>
  );
}

export default TemplatePreviewModal;
