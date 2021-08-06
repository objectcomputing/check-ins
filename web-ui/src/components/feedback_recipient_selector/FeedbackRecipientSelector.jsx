import React, {useContext, useEffect, useState, useRef} from "react";
import { makeStyles } from "@material-ui/core/styles";
import "./FeedbackRecipientSelector.css";
import FeedbackRecipientCard from "../feedback_recipient_card/FeedbackRecipientCard";
import {AppContext} from "../../context/AppContext";
import {selectProfile, selectCsrfToken, selectNormalizedMembers} from "../../context/selectors";
import {getFeedbackSuggestion} from "../../api/feedback";
import { selectCurrentUser } from "../../context/selectors";
import Typography from "@material-ui/core/Typography";
import {TextField, Grid, InputAdornment} from "@material-ui/core";
import {Search} from "@material-ui/icons";
import PropTypes from "prop-types";


const useStyles = makeStyles({
  search: {
    display: "flex",
    justifyContent: "space-between",
    alignItems: "center",
  },
  searchInput: {
    width: "20em",
  },
  searchInputIcon: {
    color: "gray",
  },
  members: {
    display: "flex",
    flexWrap: "wrap",
    justifyContent: "space-evenly",
    width: "100%",
  },
  textField: {
    width: "40ch",
        ['@media (max-width:767px)']: { // eslint-disable-line no-useless-computed-key
            width: '100%',
      },
  },
});

const propTypes = {
  changeQuery: PropTypes.func.isRequired,
  fromQuery: PropTypes.array.isRequired
}

const FeedbackRecipientSelector = ({changeQuery, fromQuery}) => {
  const { state } = useContext(AppContext);
  const classes = useStyles();
  const csrf = selectCsrfToken(state);
  const userProfile = selectCurrentUser(state);
  const {id} = userProfile;
  const searchTextUpdated = useRef(false)
  const hasRenewedFromURL = useRef(false)
  const [searchText, setSearchText] = useState("");
  const [profiles, setProfiles] = useState([]);

  useEffect(() => {
    if (!searchTextUpdated.current && searchText.length !== 0 && searchText !== "" && searchText) {
      let normalizedMembers = selectNormalizedMembers(state, searchText);
      if (fromQuery !== undefined) {
        let selectedMembers = profiles.filter(profile => fromQuery.includes(profile.id));
        let filteredNormalizedMembers = normalizedMembers.filter(member => {
          return !selectedMembers.some(selectedMember => {
            return selectedMember.id === member.id
          });
        });
        let newProfiles = selectedMembers.concat(filteredNormalizedMembers)
        setProfiles(newProfiles);
      } else {
        setProfiles(normalizedMembers)
      }
      searchTextUpdated.current = true
    }
  }, [searchText, profiles, fromQuery, state])

  useEffect(() => {
    function bindFromURL() {
      if (!hasRenewedFromURL.current && fromQuery !== null && fromQuery !== undefined) {
        let profileCopy = profiles;
        if (typeof fromQuery === 'string') {
          let newProfile = {id: fromQuery}
        if (profiles.filter(member => member.id === newProfile.id).length === 0) {
          profileCopy.push(newProfile)
        }
      } else if (Array.isArray(fromQuery)) {
          for (let i = 0; i < fromQuery.length; ++i) {
           let newProfile = {id: fromQuery[i]}
            if (profiles.filter(member => member.id === newProfile.id).length === 0) {
              profileCopy.push(newProfile)
            }
          }
      }
      setProfiles(profileCopy)
      hasRenewedFromURL.current = true
    }}

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

    if (csrf && (searchText === "" || searchText.length === 0)) {
      getSuggestions().then((res) => {
        bindFromURL();
        if (res !== undefined && res !== null) {
          let filteredProfileCopy = profiles.filter(member => {
            return !res.some(suggestedMember => {
              return suggestedMember.id === member.id
            });
          })
          let newProfiles = filteredProfileCopy.concat(res)
          setProfiles(newProfiles)
        }
      })
    }// eslint-disable-next-line react-hooks/exhaustive-deps
  },[id, csrf, searchText]);

  const cardClickHandler = (id) => {
    if(!Array.isArray(fromQuery)) {
        fromQuery = fromQuery ? [fromQuery] : [];
    }
    if(fromQuery.includes(id)) {
      fromQuery.splice(fromQuery.indexOf(id), 1);
    }
    else fromQuery.push(id);

    changeQuery("from", fromQuery);
    hasRenewedFromURL.current = false;
  }

  const getSelectedCards = () => {
    if (profiles) {
      // Get all the selected templates
      const selected = profiles.filter((profile) => fromQuery && fromQuery.includes(profile.id));
      const title = (
        <Typography style={{fontWeight: "bold", color: "#454545", marginBottom: "1em"}} variant="h5">
          {selected.length} recipient{selected.length === 1 ? "" : "s"} selected
        </Typography>
      );

      // If there are no recipients selected, show a message
      if (selected.length === 0) {
        return (
          <React.Fragment>
            {title}
            <p style={{color: "gray"}}>Click on recipients to request feedback from them</p>
          </React.Fragment>
        );
      }

      // If there are any selected recipients, display them
      return (
        <React.Fragment>
          {title}
          <div className="recipient-card-container">
            {selected.map((profile) => (
              <FeedbackRecipientCard
                key={profile.id}
                profileId={profile.id}
                recipientProfile={selectProfile(state, profile.id)}
                reason={profile.reason}
                selected
                onClick={() => cardClickHandler(profile.id)}/>
            ))}
          </div>
        </React.Fragment>
      );
    }
  }

  return (
    <Grid className="feedback-recipient-selector">
      <Grid container spacing={3}>
        <Grid item xs={12} className={classes.search}>
          <TextField
            className={classes.searchInput}
            label="Search employees..."
            placeholder="Member Name"
            value={searchText}
            onChange={(e) => {
              setSearchText(e.target.value);
              searchTextUpdated.current = false;
            }}
            InputProps={{
              startAdornment: (
                <InputAdornment className={classes.searchInputIcon} position="start">
                  <Search/>
                </InputAdornment>
              )
            }}
          />
        </Grid>
      </Grid>
      <div className="selected-recipients-container">
        {getSelectedCards()}
      </div>
      <div className="selectable-recipients-container">
        {profiles ?
          <div className="recipient-card-container">
            {profiles.filter((profile) => !fromQuery || !fromQuery.includes(profile.id)).map((profile) => (
              <FeedbackRecipientCard
                key={profile.id}
                recipientProfile = {selectProfile(state, profile.id)}
                reason={profile?.reason ? profile.reason : null}
                onClick={() => cardClickHandler(profile.id)}/>
            ))}
          </div> :
          <p>Can't get suggestions, please come back later :(</p>}
      </div>
    </Grid>
  );
};

FeedbackRecipientSelector.propTypes = propTypes;

export default FeedbackRecipientSelector;
