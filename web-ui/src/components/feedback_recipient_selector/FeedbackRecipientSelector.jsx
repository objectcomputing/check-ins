import React, {useContext, useEffect, useState} from "react";
import { makeStyles } from "@material-ui/core/styles";
import "./FeedbackRecipientSelector.css";
import FeedbackRecipientCard from "../feedback_request/Feedback_recipient_card";
import {AppContext} from "../../context/AppContext";
import {selectCsrfToken, selectNormalizedMembers, selectProfile,} from "../../context/selectors";
import {useHistory, useLocation} from "react-router-dom";
import queryString from "query-string";
import {getFeedbackSuggestion} from "../../api/feedback";
import { selectCurrentUser } from "../../context/selectors";
import {TextField, Grid } from "@material-ui/core";


const useStyles = makeStyles({
  search: {
    display: "flex",
    justifyContent: "space-between",
    alignItems: "center",
  },
  searchInput: {
    width: "20em",
  },
  members: {
    display: "flex",
    flexWrap: "wrap",
    justifyContent: "space-evenly",
    width: "100%",
  },
});

const FeedbackRecipientSelector = () => {
  const { state } = useContext(AppContext);
  const classes = useStyles();
  const csrf = selectCsrfToken(state);
  const userProfile = selectCurrentUser(state);
  const {id} = userProfile;
  const history = useHistory();
  const location = useLocation();
  const parsed = queryString.parse(location?.search);
  let from = parsed.from;
  const [searchText, setSearchText] = useState("");
  let setter = []
  if (from !== undefined && from !== null) {
    console.log("From is not undefined and from is not null")
    console.log("From in the check " + from)
    if (typeof from === 'string') {
      console.log("from is string" + from)
      console.log()
      let profileId = from
      const newProfile = selectProfile(state, profileId);
      console.log("new profile " + newProfile)
      setter.push(newProfile)
      console.log("Setter in from is string check " + setter)
    } else if (Array.isArray(from) ) {
      console.log("from is array"+from)
      setter = from
      console.log("Setter in from is arr check " + JSON.stringify(setter))
    }
  }
  const [profiles, setProfiles] = useState(setter);


  useEffect(() => {
    const normalizedMembers = selectNormalizedMembers(state, searchText);
    console.log("normalized members " + JSON.stringify(normalizedMembers));
    let selectedMembers = profiles.filter(profile => profile.selected === true)
    console.log("selected members" + selectedMembers)
    setProfiles(selectedMembers.concat(normalizedMembers));

  }, [searchText])


    useEffect(() => {
        async function getSuggestions() {
            if (id === undefined || id === null) {
                return;
            }
            let res = await getFeedbackSuggestion(id, csrf);
            if (res && res.payload) {
                return res.payload.data && !res.error
                    ? res.payload.data
                    : undefined;
            }
            return null;
        }
        if (csrf) {
            getSuggestions().then((res) => {
              console.log(res)
              if (res !== undefined && res !== null) {
                for(let i = 0; i < res.length; ++i) {
                  res[i].id = res[i].profileId
                }
                setProfiles(profiles.concat(res));
              }
            });
        }
    },[id,csrf]);

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
    <Grid className="FeedbackRecipientSelector">
      <Grid container spacing={3}>
        <Grid item xs={12} className={classes.search}>
          <TextField
            className={classes.searchInput}
            label="Select employees..."
            placeholder="Member Name"
            value={searchText}
            onChange={(e) => {
              setSearchText(e.target.value);
            }}
          />
            </Grid>

        </Grid>
      <div className="card-container">
        {profiles ?
            profiles.map((profile) => (
                <FeedbackRecipientCard
                    key={profile.id}
                    profileId={profile.id}
                    reason={profile?.reason ? profile.reason : null}
                    selected={from && from.includes(profile.id)}
                    onClick={() => cardClickHandler(profile.id)}/>
            )) :
            <p> Can't get suggestions, please come back later :( </p>}
      </div>
    </Grid>
  );
};

export default FeedbackRecipientSelector;
