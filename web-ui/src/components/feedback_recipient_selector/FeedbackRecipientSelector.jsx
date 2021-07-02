import React, {useContext, useEffect, useState, useRef} from "react";
import { makeStyles } from "@material-ui/core/styles";
import "./FeedbackRecipientSelector.css";
import FeedbackRecipientCard from "../feedback_request/Feedback_recipient_card";
import {AppContext} from "../../context/AppContext";
import {selectCsrfToken, selectNormalizedMembers} from "../../context/selectors";
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
  const hasRenewedFromURL = useRef(false)
  const hasGottenSuggestions = useRef(false)
  const [searchText, setSearchText] = useState("");
  const [profiles, setProfiles] = useState([])



  useEffect(() => {
  if (searchText.length !== 0  && searchText !== "") {
      let normalizedMembers = selectNormalizedMembers(state, searchText);
     if (from !==undefined ) {
      let selectedMembers = profiles.filter(profile => from.includes(profile.id))
      let filteredNormalizedMembers = normalizedMembers.filter(member => {
        return !selectedMembers.some(selectedMember => {
          return selectedMember.id=== member.id
        });
      });
         setProfiles(filteredNormalizedMembers.concat(selectedMembers));
      } else {
        setProfiles(normalizedMembers)

      }

  }
  }, [searchText])

  useEffect(() => {
function bindFromURL() {
    if (!hasRenewedFromURL.current && from!==null && from!==undefined) {
      console.log("bind from URL")
      if (typeof from === 'string') {
         let profileCopy = profiles;
               let newProfile = {id : from}
                   profileCopy.push(newProfile)
               setProfiles(profileCopy)


      } else if (Array.isArray(from)) {
         let profileCopy = profiles;
          for (let i = 0; i < from.length; ++i) {
           let newProfile = {id : from[i]}
                   profileCopy.push(newProfile)
          }
         setProfiles(profileCopy)
      }
      hasRenewedFromURL.current = true
    }

}
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
              bindFromURL();
              if (res !== undefined && res !== null) {
                let filteredProfileCopy = profiles.filter(member => {
                  console.log("res " + JSON.stringify(res))
                  return !res.some(suggestedMember => {
                    return suggestedMember.id === member.id
                  });
                })
                let newProfiles = filteredProfileCopy.concat(res)
                hasGottenSuggestions.current = true
                setProfiles(newProfiles)
              }
           })
      }
    },[id, csrf]);

  const cardClickHandler = (id) => {
    if(!Array.isArray(from)) from = from ? [from] : [];
    if(from.includes(id)) {
      from.splice(from.indexOf(id), 1);
    }
    else from[from.length] = id;

    parsed.from = from;
    history.push({...location, search: queryString.stringify(parsed)});
    hasRenewedFromURL.current = false;
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
                    selected={from!== undefined && from && from.includes(profile.id)}
                    onClick={() => cardClickHandler(profile.id)}/>
            )) :
            <p> Can't get suggestions, please come back later :( </p>}
      </div>
    </Grid>
  );
};

export default FeedbackRecipientSelector;
