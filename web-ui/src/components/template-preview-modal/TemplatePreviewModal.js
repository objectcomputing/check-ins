import React from 'react';
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

const TemplatePreviewModal = ({ open, onClose, template }) => {

  const classes = useStyles();

  return (
    <div>
      <Dialog fullScreen open={open} onClose={onClose} TransitionComponent={Transition}>
        <AppBar className={classes.appBar}>
          <Toolbar>
            <IconButton edge="start" color="inherit" onClick={onClose} aria-label="close">
              <CloseIcon />
            </IconButton>
            <Typography variant="h6" className={classes.title}>
              {template ? template.title : "No Title"}
            </Typography>
          </Toolbar>
        </AppBar>

        <Typography>{template && !template.isAdHoc ? template.description : ""}</Typography>
        {(template && template.isAdHoc) ?
          <TextField
            id="standard-full-width"
            label="Please type your question."
            style={{margin: 20}}
            placeholder="How is your day going?"
            fullWidth
            margin="normal"
            InputLabelProps={{
              shrink: true,
              classes: {
                root: classes.labelRoot
              }
            }}
          />
          :
          <List>
            {template && template.questions && template.questions.map((question, index) => (
              <React.Fragment>
                <ListItem button>
                  <ListItemText primary={`Question ${index + 1}`} secondary={question}/>
                </ListItem>
                <Divider/>
              </React.Fragment>
            ))}
          </List>
        }
      </Dialog>
    </div>
  );
}

export default TemplatePreviewModal;
