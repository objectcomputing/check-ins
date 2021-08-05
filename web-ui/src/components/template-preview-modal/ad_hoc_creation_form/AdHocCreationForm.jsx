import React, {useEffect, useState} from "react";
import {FormControlLabel, TextField, Tooltip} from "@material-ui/core";
import Checkbox from '@material-ui/core/Checkbox';
import {withStyles} from '@material-ui/core/styles'
import PropTypes from "prop-types";
import CheckBoxOutlineBlankIcon from "@material-ui/icons/CheckBoxOutlineBlank";
import CheckBoxIcon from "@material-ui/icons/CheckBox";
import HelpOutlineIcon from "@material-ui/icons/HelpOutline";
const propTypes = {
  onFormChange: PropTypes.func
}

const BlueCheckbox = withStyles({
  root: {
    color: '#0000ff',
    '&$checked': {
      color: '#0000ff',
    },
  },
  checked: {},
})((props) => <Checkbox color="default" {...props} />);

const AdHocCreationForm = (props) => {

  const [title, setTitle] = useState("Ad Hoc");
  const [description, setDescription] = useState("");
  const [question, setQuestion] = useState("");
  const [isPublic, setIsPublic] = useState(false);

  useEffect(() => {
    props.onFormChange({
      title: title,
      description: description,
      question: question,
    }); // eslint-disable-next-line
  }, [title, description, question]);

  return (
    <React.Fragment>
      <TextField
        label="Title"
        placeholder="Ad Hoc"
        fullWidth
        margin="normal"
        value={title}
        onChange={(event) => {
          setTitle(event.target.value);
        }}/>
      <TextField
        label="Description"
        placeholder="Ask a single question"
        fullWidth
        margin="normal"
        value={description}
        onChange={(event) => {
          setDescription(event.target.value);
        }}/>
      <TextField
        label="Ask a feedback question"
        placeholder="How is your day going?"
        fullWidth
        multiline
        rowsMax={10}
        margin="normal"
        value={question}
        onChange={(event) => {
          setQuestion(event.target.value);
        }}/>
      <FormControlLabel
        control={
          <BlueCheckbox
            icon={<CheckBoxOutlineBlankIcon fontSize="small" />}
            checkedIcon={<CheckBoxIcon fontSize="small" />}
            name="checkedI"
            onClick={setIsPublic(!isPublic)}
          />
        }
        label="Make template public?"
      />
      <Tooltip title="Check this box if you want the template to be accessed by the public" placement="left" arrow>
        <HelpOutlineIcon style={{color: "gray"}}/>
      </Tooltip>
    </React.Fragment>
  );
}

AdHocCreationForm.propTypes = propTypes;

export default AdHocCreationForm;