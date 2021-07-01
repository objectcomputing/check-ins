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

useEffect(() => {
console.log("Profiles")
console.log(profiles)
},[profiles])


useEffect(() => {
console.log("in from use effect")
if (!hasRenewedFromURL.current && hasGottenSuggestions.current && from!==null && from!==undefined) {
console.log("from changed" + from)
let profileCopy = profiles;
  if (typeof from === 'string') {
           let newProfile = {id : from, selected: true}
           if (profileCopy.filter(e => e.id !== newProfile.id).length === 0) {
               profileCopy.push(newProfile)
           }
           setProfiles(profileCopy.reverse())


  } else if (Array.isArray(from)) {
  let profileCopy = profiles
      for (let i = 0; i < from.length; ++i) {
       let newProfile = {id : from[i], selected: true}
           if (profileCopy.filter(e => e.id !== newProfile.id).length === 0) {
               profileCopy.push(newProfile)
             }
      }
     setProfiles(profileCopy.reverse())
  }
  hasRenewedFromURL.current = true
}

},[profiles])

  useEffect(() => {
  if (searchText.length !== 0  && searchText !== "") {
      let profileCopy = profiles
      let normalizedMembers = selectNormalizedMembers(state, searchText);
      let selectedMembers = profiles.filter(profile => profile.selected !== true)
      let filteredNormalizedMembers = normalizedMembers.filter((member) => {
        return selectedMembers.some((selectedMember) => {
          return selectedMember.id !== member.id
        });
      });
      console.log("selected members" + selectedMembers)
      setProfiles(filteredNormalizedMembers);
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
            console.log("Get suggestions complete")
              let profileCopy = profiles
              console.log("Res in get suggestinos " + JSON.stringify(res))
              if (res !== undefined && res !== null) {
                 let newProfiles = []
                for(let i = 0; i < res.length; ++i) {
                   res[i].id = res[i].profileId
                   profileCopy.push(res[i])
                }
               setProfiles(profileCopy)
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
