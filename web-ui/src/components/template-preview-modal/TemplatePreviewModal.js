import React from 'react';
import { makeStyles } from '@material-ui/core/styles';
import Button from '@material-ui/core/Button';
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
            {template.isAdHoc ? "New Ad-Hoc Template" : template.title}
          </Typography>
          <Button className="ad-hoc-next-button" onClick={onSubmit} color="inherit">
            {template.isAdHoc ? "Create" : "Select"}
          </Button>
        </Toolbar>
      </AppBar>

      <div className="preview-modal-content">
      {template.isAdHoc ?
        <React.Fragment>
          <TextField
            id="standard-full-width"
            label="Title"
            placeholder="Ad Hoc"
            fullWidth
            defaultValue={template.title}
            margin="normal"/>
          <TextField
            id="standard-full-width"
            label="Description"
            placeholder="Ask a single question"
            fullWidth
            defaultValue={template.description}
            margin="normal"/>
        </React.Fragment>
        :
        <Typography>{template.description}</Typography>
      }

      {template.isAdHoc ?
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
