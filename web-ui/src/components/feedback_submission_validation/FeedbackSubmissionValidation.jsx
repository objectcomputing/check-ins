import React, {useState} from "react";

import TextField from "@material-ui/core/TextField";
import Typography from "@material-ui/core/Typography";
import {Button} from "@material-ui/core";
import {makeStyles} from "@material-ui/core/styles";

import "./FeedbackSubmissionValidation.css";

const useStyles = makeStyles({
  textField: {
    padding: "25px 0px"
  }
});

const FeedbackSubmissionValidation = () => {
  const classes = useStyles();
  let [value, setValue] = useState();

  const handleClick= () => {
    console.log(value);
  }
  const handleChange = (e) => {
    setValue(e.target.value);
  }

  return (
    <div className = "feedback-sub-validation">
      <Typography variant="body 1"><b>Please Enter your OCI email to continue</b></Typography><br/>
      <TextField
        value={value}
        className={classes.textField}
        variant="outlined"
        placeholder="Please enter your email: "
        onChange={handleChange}
      /><br/>
      <Button
        onClick={handleClick}
        variant="contained"
        color="primary">
        Submit
      </Button>
    </div>
  );
};
export default FeedbackSubmissionValidation;