import React, {useContext, useEffect, useState, useRef} from "react";
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
  const hasRenewedFromURL = useRef(false)
  const hasGottenSuggestions = useRef(false)
  const [searchText, setSearchText] = useState("");
  const [profiles, setProfiles] = useState([])
  const [selected, setSelected] = useState([])


function sortBySelected(profileCopy) {
profileCopy = profileCopy.sort(function(x,y) {
  let isXInFrom = from.includes(x)
  let isYInFrom = from.includes(y)
  return (isXInFrom === isYInFrom)? 0: isXInFrom?-1:1;

})
return profileCopy;
}
useEffect(() => {
if (!hasRenewedFromURL.current && hasGottenSuggestions && from!==null && from!==undefined) {
   let profileCopy = profiles;
  if (typeof from === 'string') {
           let newProfile = {profileId : from}
           const filteredProfiles = profileCopy.filter(e => e.profileId === newProfile.profileId)
           console.log("Result of filtering" + JSON.stringify(filteredProfiles))
           if (filteredProfiles.length === profileCopy.length) {
               profileCopy.push(newProfile)
           }
           setProfiles(profileCopy)
           setSelected(selected => [...selected, newProfile])


  } else if (Array.isArray(from)) {
      for (let i = 0; i < from.length; ++i) {
       let newProfile = {profileId : from[i]}
        setSelected(selected => [...selected, newProfile])
          let filteredProfiles = profileCopy.filter(e => e.profileId === newProfile.profileId)
          console.log(filteredProfiles)
           if (filteredProfiles.length === profileCopy.length) {
               profileCopy.push(newProfile)
             }
      }
     let newProfiles = sortBySelected(profileCopy)
     setProfiles(profileCopy)
  }
  hasRenewedFromURL.current = true
}

},[from])

  useEffect(() => {
  if (searchText.length !== 0  && searchText !== "") {
      let normalizedMembers = selectNormalizedMembers(state, searchText);
      normalizedMembers.forEach(element => element.profileId = element.id)
      let selectedMembers=[];
      let filteredNormalizedMembers;
     if (from !==undefined ) {
      let selectedMembers = profiles.filter(profile => from.includes(profile.profileId))
      let filteredNormalizedMembers = normalizedMembers.filter(member => {
        return !selectedMembers.some(selectedMember => {
          return selectedMember.profileId === member.profileId
        });
      });
            setProfiles(filteredNormalizedMembers.concat(selectedMembers));
      } else {
        setProfiles(normalizedMembers)

      }

  }
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
              console.log("Res in get suggestinos " + JSON.stringify(res))
              if (res !== undefined && res !== null) {
              let profileCopy = profiles
                setProfiles(profileCopy.concat(res))
                hasGottenSuggestions.current = true
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
                    key={profile.profileId}
                    profileId={profile.profileId}
                    reason={profile?.reason ? profile.reason : null}
                    selected={from!== undefined && from && from.includes(profile.profileId)}
                    onClick={() => cardClickHandler(profile.profileId)}/>
            )) :
            <p> Can't get suggestions, please come back later :( </p>}
      </div>
    </Grid>
  );
};

export default FeedbackRecipientSelector;
