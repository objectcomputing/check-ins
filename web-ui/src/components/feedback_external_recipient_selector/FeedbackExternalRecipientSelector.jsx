import React, { useContext, useEffect, useState, useRef } from 'react';
import { styled } from '@mui/material/styles';
import FeedbackExternalRecipientCard from '../feedback_external_recipient_card/FeedbackExternalRecipientCard';
import { AppContext } from '../../context/AppContext';
import {
  selectProfile,
  selectCsrfToken,
  selectCurrentUser,
  selectNormalizedMembers
} from '../../context/selectors';
import {getExternalRecipients} from '../../api/feedback';
import Typography from '@mui/material/Typography';
import { TextField, Grid, InputAdornment } from '@mui/material';
import { Search } from '@mui/icons-material';
import PropTypes from 'prop-types';
import './FeedbackExternalRecipientSelector.css';

const PREFIX = 'FeedbackExternalRecipientSelector';
const classes = {
  search: `${PREFIX}-search`,
  searchInput: `${PREFIX}-searchInput`,
  searchInputIcon: `${PREFIX}-searchInputIcon`,
  members: `${PREFIX}-members`,
  textField: `${PREFIX}-textField`
};

const StyledGrid = styled(Grid)({
  [`& .${classes.search}`]: {
    display: 'flex',
    justifyContent: 'space-between',
    alignItems: 'center'
  },
  [`& .${classes.searchInput}`]: {
    width: '20em'
  },
  [`& .${classes.searchInputIcon}`]: {
    color: 'gray'
  },
  [`& .${classes.members}`]: {
    display: 'flex',
    flexWrap: 'wrap',
    justifyContent: 'space-evenly',
    width: '100%'
  }
});

const propTypes = {
  changeQuery: PropTypes.func.isRequired,
  fromQuery: PropTypes.array.isRequired,
  forQuery: PropTypes.string.isRequired
};

const FeedbackExternalRecipientSelector = ({ changeQuery, fromQuery, forQuery }) => {
  const { state } = useContext(AppContext);
  const csrf = selectCsrfToken(state);
  const userProfile = selectCurrentUser(state);
  const { id } = userProfile;
  const searchTextUpdated = useRef(false);
  const hasRenewedFromURL = useRef(false);
  const [searchText, setSearchText] = useState('');
  const [profiles, setProfiles] = useState([]);
  const normalizedMembers = selectNormalizedMembers(state, searchText);

  useEffect(() => {
    if (
        !searchTextUpdated.current &&
        searchText.length !== 0 &&
        searchText !== '' &&
        searchText
    ) {
      if (fromQuery !== undefined) {
        let selectedMembers = profiles.filter(profile =>
            fromQuery.includes(profile.id)
        );
        let filteredNormalizedMembers = normalizedMembers.filter(member => {
          return !selectedMembers.some(selectedMember => {
            return selectedMember.id === member.id;
          });
        });
        setProfiles(filteredNormalizedMembers);
      } else {
        setProfiles(normalizedMembers);
      }
      searchTextUpdated.current = true;
    }
  }
  , [searchText, profiles, fromQuery, state, userProfile, normalizedMembers])
  ;

  useEffect(() => {
    function bindFromURL() {
      if (
          !hasRenewedFromURL.current &&
          fromQuery !== null &&
          fromQuery !== undefined
      ) {
        let profileCopy = profiles;
        console.log("FeedbackExternalRecipientSelector, bindFromURL, profiles: ", profiles);
        console.log("FeedbackExternalRecipientSelector, bindFromURL, fromQuery: ", fromQuery);
        if (typeof fromQuery === 'string') {
          let newProfile = { id: fromQuery };
          console.log("FeedbackExternalRecipientSelector, bindFromURL, newProfile: ", newProfile);
          if (profiles.filter(member => member.id === newProfile.id).length === 0) {
            profileCopy.push(newProfile);
          }
        } else if (Array.isArray(fromQuery)) {
          for (let i = 0; i < fromQuery.length; ++i) {
            let newProfile = { id: fromQuery[i] };
            if (profiles.filter(member => member.id === newProfile.id).length === 0) {
              profileCopy.push(newProfile);
            }
          }
        }
        setProfiles(profileCopy);
        hasRenewedFromURL.current = true;
      }
    }

    async function getExternalRecipientsForSelector() {
      if (forQuery === undefined || forQuery === null) {
        return;
      }
      let res = await getExternalRecipients(csrf);
      if (res && res.payload) {
        return res.payload.data && !res.error ? res.payload.data : undefined;
      }
      return null;
    }

    if (csrf && (searchText === '' || searchText.length === 0)) {
      getExternalRecipientsForSelector().then(res => {
        bindFromURL();
        if (res !== undefined && res !== null) {
          let filteredProfileCopy = profiles.filter(member => {
            return !res.some(suggestedMember => {
              return suggestedMember.id === member.id;
            });
          });
          let newProfiles = filteredProfileCopy.concat(res);
          console.log("FeedbackExternalRecipientSelector, getExternalRecipientsForSelector, newProfiles: ", newProfiles);
          setProfiles(newProfiles);
        }
      });
    } // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [id, csrf, searchText])
  ;

  const cardClickHandler = id => {
    console.log("FeedbackExternalRecipientSelector, cardClickHandler, id: ", id);
    console.log("FeedbackExternalRecipientSelector, cardClickHandler, fromQuery: ", fromQuery);
    if (!Array.isArray(fromQuery)) {
      fromQuery = fromQuery ? [fromQuery] : [];
    }
    if (fromQuery.includes(id)) {
      fromQuery.splice(fromQuery.indexOf(id), 1);
    } else {
      fromQuery.push(id);
    }

    changeQuery('from', fromQuery);
    hasRenewedFromURL.current = false;
  };

  const getSelectedCards = () => {
    if (fromQuery) {
      const title = (
          <Typography
              style={{ fontWeight: 'bold', color: '#454545', marginBottom: '1em' }}
              variant="h5"
          >
            {fromQuery.length} recipient
            {fromQuery.length === 1 ? '' : 's'} selected
          </Typography>
      );

      // If there are no recipients selected, show a message
      if (fromQuery.length === 0) {
        return (
            <>
              {title}
              <p style={{ color: 'gray' }}>
                Click on external recipients to request feedback from them
              </p>
            </>
        );
      }

      // If there are any selected recipients, display them
      return (
          <>
            {title}
            <div className="recipient-card-container">
              {fromQuery.map(id => (
                  <FeedbackExternalRecipientCard
                      key={id}
                      profileId={id}
                      recipientProfile={selectProfile(state, id)}
                      selected
                      onClick={() => cardClickHandler(id)}
                  />
              ))}
            </div>
          </>
      );
    }
  };

  return (
      <StyledGrid className="feedback-recipient-selector">
        <Grid container spacing={3}>
          <Grid item xs={12} className={classes.search}>
            <TextField
                className={classes.searchInput}
                label="Search external recipients..."
                placeholder="Recipient Name"
                value={searchText}
                onChange={e => {
                  setSearchText(e.target.value);
                  searchTextUpdated.current = false;
                }}
                InputProps={{
                  startAdornment: (
                      <InputAdornment
                          className={classes.searchInputIcon}
                          position="start"
                      >
                        <Search />
                      </InputAdornment>
                  )
                }}
            />
          </Grid>
        </Grid>
        <div className="selected-recipients-container">{getSelectedCards()}</div>
        <div className="selectable-recipients-container">
          {profiles ? (
              <div className="recipient-card-container">
                {profiles
                    .filter(
                        profile =>
                            !fromQuery ||
                            (!fromQuery.includes(profile.id) && profile.id !== forQuery)
                    )
                    .map(profile => (
                        <FeedbackExternalRecipientCard
                            key={profile.id}
                            recipientProfile={profile}
                            onClick={() => cardClickHandler(profile.id)}
                        />
                    ))}
              </div>
          ) : (
              <p>Can't get suggestions, please come back later :(</p>
          )}
        </div>
      </StyledGrid>
  );

  //recipientProfile={selectProfile(state, profile.id)}

};

FeedbackExternalRecipientSelector.propTypes = propTypes;

export default FeedbackExternalRecipientSelector;
