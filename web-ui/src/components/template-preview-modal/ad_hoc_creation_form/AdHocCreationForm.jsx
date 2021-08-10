import React, {useEffect, useState} from "react";
import {FormControlLabel, TextField, Tooltip} from "@material-ui/core";
import Checkbox from '@material-ui/core/Checkbox';
import PropTypes from "prop-types";
import CheckBoxOutlineBlankIcon from "@material-ui/icons/CheckBoxOutlineBlank";
import CheckBoxIcon from "@material-ui/icons/CheckBox";
import HelpOutlineIcon from "@material-ui/icons/HelpOutline";
const propTypes = {
  onFormChange: PropTypes.func
}

const AdHocCreationForm = (props) => {

  const [title, setTitle] = useState("Ad Hoc");
  const [description, setDescription] = useState("");
  const [question, setQuestion] = useState("");
  const [isPublic, setIsPublic] = useState(true);

  useEffect(() => {
    props.onFormChange({
      title: title,
      description: description,
      question: question,
      isPublic: isPublic,
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
      <div className="public-checkbox-container">
        <FormControlLabel
          control={
            <Checkbox
              color="primary"
              icon={<CheckBoxOutlineBlankIcon fontSize="small" />}
              checkedIcon={<CheckBoxIcon fontSize="small" />}
              name="publicChecked"
              onClick={() => setIsPublic(!isPublic)}
            />
          }
          label="Make template public"
        />
        <Tooltip title="Private templates can only be used by you and admins. Public templates can be used by anyone." placement="bottom" arrow>
          <HelpOutlineIcon style={{color: "gray"}}/>
        </Tooltip>
      </div>
    </React.Fragment>
  );
}

AdHocCreationForm.propTypes = propTypes;

export default AdHocCreationForm;