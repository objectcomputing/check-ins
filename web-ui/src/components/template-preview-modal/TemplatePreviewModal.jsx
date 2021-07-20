import React, {useState} from 'react';
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
import Divider from "@material-ui/core/Divider";
import Button from "@material-ui/core/Button"
import "./TemplatePreviewModal.css";
import AdHocCreationForm from "./ad_hoc_creation_form/AdHocCreationForm";
import PropTypes from "prop-types";

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

const propTypes = {
  open: PropTypes.bool.isRequired,
  onSubmit: PropTypes.func,
  onClose: PropTypes.func,
  template: PropTypes.object,
  createAdHoc: PropTypes.bool
}

const TemplatePreviewModal = ({ open, onSubmit, onClose, template, createAdHoc }) => {

  const classes = useStyles();

  const [newAdHocData, setNewAdHocData] = useState({});

  const submitPreview = () => {
    const submittedTemplate = {...template};
    if (createAdHoc) {
      submittedTemplate.title = newAdHocData.title;
      submittedTemplate.description = newAdHocData.description;
    }
    onSubmit(submittedTemplate);
  };

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
      {createAdHoc ?
        <AdHocCreationForm onFormChange={(form) => setNewAdHocData(form)}/> :
        <React.Fragment>
          <Typography>{template.description}</Typography>
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
        </React.Fragment>
      }
      </div>
    </Dialog>
  );
}

TemplatePreviewModal.propTypes = propTypes;

export default TemplatePreviewModal;