import React, {useContext, useEffect, useState} from "react";
import { makeStyles } from "@material-ui/core/styles";
import TextField from "@material-ui/core/TextField";
import InputAdornment from "@material-ui/core/InputAdornment";
import Search from "@material-ui/icons/Search";

import "./FeedbackRecipientSelector.css";
import FeedbackRecipientCard from "../feedback_request/Feedback_recipient_card";
import {AppContext} from "../../context/AppContext";
import {selectCsrfToken} from "../../context/selectors";
import {useHistory, useLocation} from "react-router-dom";
import queryString from "query-string";
import {getFeedbackSuggestion} from "../../api/feedback";
import { selectCurrentUser } from "../../context/selectors";

const useStyles = makeStyles({
  root: {
    color: "gray"
  },
  textField: {
    width: "40ch"
  }
});

const FeedbackRecipientSelector = () => {
    const { state } = useContext(AppContext);
    const csrf = selectCsrfToken(state);
    const userProfile = selectCurrentUser(state);
    const {id} = userProfile;
    const classes = useStyles();
    const history = useHistory();
    const location = useLocation();
    const parsed = queryString.parse(location?.search);
    let from = parsed.from;
    const [profiles, setProfiles] = useState([]);

    useEffect(() => {
        async function getSuggestions() {
            if (id === undefined || id === null) {
                return;
            }
            let res = await getFeedbackSuggestion(id, csrf);
            if (res && res.payload) {
                console.log(res.payload);
                return res.payload.data && !res.error
                    ? res.payload.data
                    : undefined;
            }
            return null;
        }
        if (csrf) {
            getSuggestions().then((res) => {
                setProfiles(res);
            });
        }
    },[id]);

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
        {profiles ?
            profiles.map((profile) => (
                <FeedbackRecipientCard
                    key={profile.profileId}
                    profileId={profile.profileId}
                    reason={profile.reason}
                    selected={from && from.includes(profile.profileId)}
                    onClick={() => cardClickHandler(profile.profileId)}/>
            )) :
            <p> Can't get suggestions, please come back later :( </p>}
      </div>
    </div>
  )
}

export default FeedbackRecipientSelector;