import React, { useContext, useEffect, useState } from 'react';
import { useHistory } from 'react-router-dom';
import { getMembersByPDL } from '../../api/member';
import { getCheckinByMemberId } from '../../api/checkins';
import { AppContext } from '../../context/AppContext';
import { UPDATE_CHECKINS } from '../../context/actions';
import {
  selectCurrentUserId,
  selectMostRecentCheckin,
  selectCsrfToken
} from '../../context/selectors';
import Card from '@mui/material/Card';
import CardHeader from '@mui/material/CardHeader';
import List from '@mui/material/List';
import ListItem from '@mui/material/ListItem';
import ListItemText from '@mui/material/ListItemText';
import ListItemAvatar from '@mui/material/ListItemAvatar';
import GroupIcon from '@mui/icons-material/Group';
import Avatar from '../avatar/Avatar';
import { getAvatarURL } from '../../api/api.js';

import './Personnel.css';

const Personnel = () => {
  const { state, dispatch } = useContext(AppContext);
  const history = useHistory();
  const csrf = selectCsrfToken(state);
  const id = selectCurrentUserId(state);
  const [personnel, setPersonnel] = useState();

  // Get personnel
  useEffect(() => {
    async function updatePersonnel() {
      if (id) {
        let res = await getMembersByPDL(id, csrf);
        let data =
          res.payload &&
          res.payload.data &&
          res.payload.status === 200 &&
          !res.error
            ? res.payload.data
            : null;
        if (data) {
          setPersonnel(data);
        }
      }
    }
    if (csrf) {
      updatePersonnel();
    }
  }, [csrf, id]);

  // Get checkins per personnel
  useEffect(() => {
    async function updateCheckins() {
      if (personnel) {
        for (const person of personnel) {
          let res = await getCheckinByMemberId(person.id, csrf);
          let data =
            res && res.payload && res.payload.status === 200
              ? res.payload.data
              : null;
          if (data && data.length > 0 && !res.error) {
            dispatch({ type: UPDATE_CHECKINS, payload: data });
          }
        }
      }
    }
    if (csrf) {
      updateCheckins();
    }
  }, [csrf, personnel, dispatch]);

  // Create feedback request link
  const createFeedbackRequestLink = memberId => (
    <span
      className="feedback-link"
      onClick={e => {
        e.stopPropagation();
        history.push(`/feedback/request?for=${memberId}`);
      }}
    >
      Request Feedback
    </span>
  );

  // Create entry of member and their last checkin
  function createEntry(person, lastCheckin, keyInput) {
    let key = keyInput ? keyInput : undefined;
    let name = 'Team Member';
    let workEmail = '';

    if (person) {
      let id = person.id ? person.id : null;
      name = person.name ? person.name : id ? id : name;
      workEmail = person.workEmail;
      key = id && !key ? `${id}Personnel` : key;
    }

    return (
      <ListItem key={key}>
        <ListItemAvatar>
          <Avatar
            alt={name}
            src={getAvatarURL(workEmail)}
            onClick={() => {
              history.push(`/checkins/${person?.id}`);
            }}
          />
        </ListItemAvatar>
        <ListItemText
          primary={name}
          secondary={createFeedbackRequestLink(person.id)}
        />
      </ListItem>
    );
  }

  // Create the entries for the personnel container
  const createPersonnelEntries = () => {
    if (personnel && personnel.length > 0) {
      return personnel.map(person =>
        createEntry(person, selectMostRecentCheckin(state, person.id), null)
      );
    } else {
      // If no personnel, show the filler message
      return (
        <ListItem>
          <ListItemText
            primary={
              <em>Your assigned development partners are shown here.</em>
            }
          />
        </ListItem>
      );
    }
  };

  return (
    <Card>
      <CardHeader avatar={<GroupIcon />} title="Development Partners" />
      <List dense>{createPersonnelEntries()}</List>
    </Card>
  );
};

export default Personnel;