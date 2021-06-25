import React, {useContext} from "react";
import { makeStyles } from "@material-ui/core/styles";
import TextField from "@material-ui/core/TextField";
import InputAdornment from "@material-ui/core/InputAdornment";
import Search from "@material-ui/icons/Search";
import "./FeedbackRecipientSelector.css";
import FeedbackRecipientCard from "../feedback_request/Feedback_recipient_card";
import {AppContext} from "../../context/AppContext";
import {selectCurrentMembers} from "../../context/selectors";
import {useHistory, useLocation} from "react-router-dom";
import queryString from "query-string";

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
  const {state} = useContext(AppContext);
  const profiles = selectCurrentMembers(state);
  const history = useHistory();
  const location = useLocation();
  const parsed = queryString.parse(location?.search);
  let from = parsed.from;

  const cardClickHandler = (id) => {
    if(!Array.isArray(from)) from = from ? [from] : [];
    if(from.includes(id)) {
      from.splice(from.indexOf(id), 1);
    }
    else from[from.length] = id;

    parsed.from = from;
    history.push({...location, search: queryString.stringify(parsed)});
  }

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
        {profiles && profiles.map((profile) => (
          <FeedbackRecipientCard profileId={profile.id} reason={undefined} selected={from && from.includes(profile.id)} onClick ={() => cardClickHandler(profile.id)}/>
        ))}
      </div>
    </div>
  )
}

export default FeedbackRecipientSelector;