import React from "react";
import { makeStyles } from "@material-ui/core/styles";
import TextField from "@material-ui/core/TextField";
import InputAdornment from "@material-ui/core/InputAdornment";
import Search from "@material-ui/icons/Search";

import "./FeedbackRecipientSelector.css";

const useStyles = makeStyles({
  root: {
    color: "gray"
  },
  textField: {
    width: "40ch"
  }
});

const FeedbackRecipientSelector = () => {
  const classes = useStyles();

  return (
    <div className="feedback-recipient-selector">
      <TextField
        className={classes.textField}
        placeholder="Search..."
        InputProps={{
          startAdornment: (
            <InputAdornment className={classes.root} position="start">
              <Search />
            </InputAdornment>
          ),
        }}
      />
      <div className="card-container">
        {/* Cards rendered here */}
      </div>
    </div>
  )
}

export default FeedbackRecipientSelector;