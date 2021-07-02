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


function sortBySelected(profileCopy) {
profileCopy = profileCopy.sort(function(x,y) {
  let isXInFrom = from.includes(x)
  console.log("is x in from?? " + isXInFrom)
  let isYInFrom = from.includes(y)
  console.log("is y in from?? " + isXInFrom)
  return (isXInFrom === isYInFrom)? 0: isXInFrom?-1:1;

})
return profileCopy;
}
// useEffect(() => {
// if (!hasRenewedFromURL.current && hasGottenSuggestions && from!==null && from!==undefined) {
//   if (typeof from === 'string') {
//      let profileCopy = profiles;
//      console.log("profile copy before " +profileCopy)
//            let newProfile = {profileId : from}
//            const filteredProfiles = profileCopy.filter(e => e.profileId === newProfile.profileId)
//            console.log("Result of filtering" + JSON.stringify(filteredProfiles))
//            if (filteredProfiles.length < 1) {
//                profileCopy.push(newProfile)
//            }
//            setProfiles(profileCopy)
//
//
//   } else if (Array.isArray(from)) {
//      let profileCopy = profiles;
//      console.log(profileCopy)
//       for (let i = 0; i < from.length; ++i) {
//        let newProfile = {profileId : from[i]}
//           let filteredProfiles = profileCopy.filter(e => e.profileId === newProfile.profileId)
//           console.log(filteredProfiles)
//            if (filteredProfiles.length < 1) {
//            console.log("push " + JSON.stringify(newProfile) + "to the profiles ")
//                profileCopy.push(newProfile)
//              }
//       }
//      let newProfiles = sortBySelected(profileCopy)
//      setProfiles(newProfiles)
//   }
//   hasRenewedFromURL.current = true
// }
//
// },[profiles])

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
function bindFromURL() {
    if (!hasRenewedFromURL.current && from!==null && from!==undefined) {
      if (typeof from === 'string') {
         let profileCopy = profiles;
         console.log("profile copy before " +profileCopy)
               let newProfile = {profileId : from}
               const filteredProfiles = profileCopy.filter(e => e.profileId === newProfile.profileId)
               console.log("Result of filtering" + JSON.stringify(filteredProfiles))
               if (filteredProfiles.length < 1) {
                   profileCopy.push(newProfile)
               }
               setProfiles(profileCopy)


      } else if (Array.isArray(from)) {
         let profileCopy = profiles;
         console.log(profileCopy)
          for (let i = 0; i < from.length; ++i) {
           let newProfile = {profileId : from[i]}
              let filteredProfiles = profileCopy.filter(e => e.profileId === newProfile.profileId)
              console.log(filteredProfiles)
               if (filteredProfiles.length < 1) {
               console.log("push " + JSON.stringify(newProfile) + "to the profiles ")
                   profileCopy.push(newProfile)
                 }
          }
         let newProfiles = sortBySelected(profileCopy)
         setProfiles(newProfiles)
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
          hasGottenSuggestions.current = false
            getSuggestions().then((res) => {
            let filteredProfiles=[]
              console.log("Res in get suggestinos " + JSON.stringify(res))
              if (res !== undefined && res !== null) {
              let profileCopy = profiles
              console.log("Profile copy before in res " + JSON.stringify(profiles))
              for (let i = 0; i < res.length; ++i ) {
                let element = res[i]
                filteredProfiles = profileCopy.filter(e => e.profileId !== res[i].profileId)
                console.log("Filtered profiles in res " + JSON.stringify(filteredProfiles))
              }
                setProfiles(filteredProfiles.concat(res))
                hasGottenSuggestions.current = true
              }
           }).then(() => {
           bindFromURL();
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
