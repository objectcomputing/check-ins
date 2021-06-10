import React from "react";
import { makeStyles } from "@material-ui/core/styles";
import TextField from "@material-ui/core/TextField";
import InputAdornment from "@material-ui/core/InputAdornment";
import Search from "@material-ui/icons/Search";

const useStyles = makeStyles({
  root: {
    color: "gray"
  }
});

const FeedbackRecipientSelector = () => {
  const classes = useStyles();

  return (
    <div className="feedback-recipient-selector">
      <TextField
        id="recipient-search"
        placeholder="Search..."
        InputProps={{
          startAdornment: (
            <InputAdornment className={classes.root} position="start">
              <Search />
            </InputAdornment>
          ),
        }}
      />
    </div>
  )
}

export default FeedbackRecipientSelector;