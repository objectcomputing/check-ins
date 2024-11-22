import React, { useContext, useEffect, useState, useRef } from 'react';
import { styled } from '@mui/material/styles';
import FeedbackExternalRecipientCard from '../feedback_external_recipient_card/FeedbackExternalRecipientCard';
import { AppContext } from '../../context/AppContext';
import {
    selectProfile, selectCsrfToken, selectCurrentUser, selectNormalizedMembers,
} from '../../context/selectors';
import {putExternalRecipientInactivate, getExternalRecipients} from '../../api/feedback';
import Typography from '@mui/material/Typography';
import { TextField, Grid, InputAdornment } from '@mui/material';
import { Search } from '@mui/icons-material';
import PropTypes from 'prop-types';
import './FeedbackExternalRecipientSelector.css';
import Button from '@mui/material/Button';
import Tooltip from '@mui/material/Tooltip';
import HelpOutlineIcon from '@mui/icons-material/HelpOutline';
import {createFeedbackExternalRecipient, createFeedbackTemplateWithQuestion} from "../../api/feedbacktemplate.js";
import NewExternalRecipientModal from "./NewExternalRecipientModal.jsx";
import {UPDATE_TOAST} from "../../context/actions.js";


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
    forQuery: PropTypes.string.isRequired,
    addExternalRecipientId: PropTypes.func.isRequired,
};

const FeedbackExternalRecipientSelector = ({ changeQuery, fromQuery, forQuery, addExternalRecipientId }) => {
  const { state, dispatch } = useContext(AppContext);
  const csrf = selectCsrfToken(state);
  const userProfile = selectCurrentUser(state);
  const { id } = userProfile;
  const searchTextUpdated = useRef(false);
  const hasRenewedFromURL = useRef(false);
  const [searchText, setSearchText] = useState('');
  const [externalRecipients, setExternalRecipients] = useState([]);


    useEffect(() => {

        if (!searchTextUpdated.current && searchText.length !== 0 && searchText !== '' && searchText) {
          if (fromQuery !== undefined) {
            let selectedRecipients = externalRecipients.filter(externalRecipient =>
                fromQuery.includes(externalRecipient.id)
            );
            const normalizedRecipients = [...externalRecipients];
              const filteredNormalizedMembers = normalizedRecipients.filter(member => {
                  const searchTextLower = searchText.toLowerCase();
                  const matchesSearchText = ['firstName', 'lastName', 'email', 'companyName'].some(key =>
                      member[key] && member[key].toLowerCase().includes(searchTextLower)
                  );
                  const isInFromQuery = fromQuery.includes(member.id);
                  return matchesSearchText || isInFromQuery;
              });
            setExternalRecipients(filteredNormalizedMembers);
          } else {
            setExternalRecipients(normalizedRecipients);
          }
          searchTextUpdated.current = true;
        }
    }, [searchText, externalRecipients, fromQuery, state, userProfile])
    ;

    useEffect(() => {
        function bindFromURL() {
            if (
              !hasRenewedFromURL.current &&
              fromQuery !== null &&
              fromQuery !== undefined
            ) {
            let profileCopy = externalRecipients;
            if (typeof fromQuery === 'string') {
              let newProfile = { id: fromQuery };
              if (externalRecipients.filter(externalRecipient => externalRecipient.id === newProfile.id).length === 0) {
                profileCopy.push(newProfile);
              }
            } else if (Array.isArray(fromQuery)) {
              for (let i = 0; i < fromQuery.length; ++i) {
                let newProfile = { id: fromQuery[i] };
                if (externalRecipients.filter(externalRecipient => externalRecipient.id === newProfile.id).length === 0) {
                  profileCopy.push(newProfile);
                }
              }
            }
            setExternalRecipients(profileCopy);
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
          let filteredProfileCopy = externalRecipients.filter(member => {
            return !res.some(suggestedMember => {
              return suggestedMember.id === member.id;
            });
          });
          let newProfiles = filteredProfileCopy.concat(res);
          setExternalRecipients(newProfiles);
        }
        });
        } // eslint-disable-next-line react-hooks/exhaustive-deps
    }, [id, csrf, searchText])
    ;

    const cardClickHandler = id => {
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

    async function externalRecipientEdit(externalRecipient) {
        console.log("FeedbackExternalRecipientSelector, externalRecipientEdit, externalRecipient: ", externalRecipient);
    }

    async function externalRecipientInactivate(id) {
        let externalRecipientsCopy = [...externalRecipients];
        let indexArray = externalRecipientsCopy.findIndex(profile => profile.id === id);

        try {
            const response = await putExternalRecipientInactivate(id, csrf);
            if (response && !response.error) {
                externalRecipientsCopy[indexArray].inactive = true;
                hasRenewedFromURL.current = false;
                setExternalRecipients(externalRecipientsCopy);
            } else {
                const errorMessage = 'Failed to inactivate recipient';
                dispatch({
                    type: UPDATE_TOAST,
                    payload: {
                        severity: 'error',
                        toast: errorMessage
                    }
                });
            }
        } catch (error) {
            const errorMessage = ("An error occurred while inactivating the recipient: ", error);
            dispatch({
                type: UPDATE_TOAST,
                payload: {
                    severity: 'error',
                    toast: errorMessage
                }
            });
        }
    }

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
                      recipientProfile={externalRecipients.find(profile => profile.id === id)}
                      selected
                      onClick={() => cardClickHandler(id)}
                  />
              ))}
            </div>
          </>
      );
    }
  };

    const [newRecipientModalOpen, setNewRecipientModalOpen] = useState(false);
    const handleNewRecipientOpen = () => {
        setNewRecipientModalOpen(true);
    };
    const handleNewRecipientClose = () => {
        setNewRecipientModalOpen(false);
    };
    const handleNewRecipientSubmit = async (newRecipient) => {
        const { feedbackExternalRecipientRes } =
            await createFeedbackExternalRecipient(
                newRecipient,
                csrf
            );
        if (feedbackExternalRecipientRes.error) {
            const errorMessage = 'Failed to save external recipient';
            dispatch({
                type: UPDATE_TOAST,
                payload: {
                    severity: 'error',
                    toast: errorMessage
                }
            });
        } else if (feedbackExternalRecipientRes.payload && feedbackExternalRecipientRes.payload.data) {
            newRecipient.id = feedbackExternalRecipientRes.payload.data.id;
            console.log("FeedbackExternalRecipientSelector.jsx, handleNewRecipientSubmit, newRecipient: ", newRecipient);
            //setExternalRecipients([...externalRecipients, newRecipient]);
            addExternalRecipientId(newRecipient.id);
            const updatedRecipients = await getExternalRecipients(csrf);
            console.log("FeedbackExternalRecipientSelector.jsx, handleNewRecipientSubmit, updatedRecipients: ", updatedRecipients);
            if (updatedRecipients && updatedRecipients.payload) {
                setExternalRecipients(updatedRecipients.payload.data);
            }
            handleNewRecipientClose();
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
                    <div className="new-recipient-button">
                        <Button variant="contained" color="primary" onClick={handleNewRecipientOpen}>
                            New External Recipient
                        </Button>
                        <Tooltip
                            title="Create a new external recipient"
                            arrow
                        >
                            <HelpOutlineIcon style={{ color: 'gray', marginLeft: '10px' }} />
                        </Tooltip>
                    </div>
                </Grid>
            </Grid>
            <div className="selected-recipients-container">{getSelectedCards()}</div>
            <div className="selectable-recipients-container">
                {externalRecipients ? (
                    <div className="recipient-card-container">
                        {externalRecipients
                            .filter(
                                profile =>
                                    !fromQuery ||
                                    (!fromQuery.includes(profile.id) && profile.id !== forQuery && !!!profile.inactive)
                            )
                            .map((profile, index) => (
                                <FeedbackExternalRecipientCard
                                    key={profile.id}
                                    recipientProfile={profile}
                                    onClick={() => cardClickHandler(profile.id)}
                                    onInactivateHandle={() => externalRecipientInactivate(profile.id)}
                                    onEditHandle={externalRecipientEdit}
                                />
                            ))}
                    </div>
                ) : (
                    <p>Can't get suggestions, please come back later :(</p>
                )}
            </div>
            <NewExternalRecipientModal
                open={newRecipientModalOpen}
                onClose={handleNewRecipientClose}
                onSubmit={handleNewRecipientSubmit}
            />
        </StyledGrid>
    );
  //recipientProfile={selectProfile(state, profile.id)}
};

FeedbackExternalRecipientSelector.propTypes = propTypes;

export default FeedbackExternalRecipientSelector;
