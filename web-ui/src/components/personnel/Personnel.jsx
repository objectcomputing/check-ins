import React, { useContext, useEffect, useState } from "react";
import { getMembersByPDL } from "../../api/member";
import { getCheckinByMemberId } from "../../api/checkins";
import { AppContext, UPDATE_SELECTED_PROFILE } from "../../context/AppContext";
import Card from '@material-ui/core/Card';
import CardHeader from '@material-ui/core/CardHeader';
import List from '@material-ui/core/List';
import ListItem from '@material-ui/core/ListItem';
import ListItemText from '@material-ui/core/ListItemText';
import ListItemAvatar from '@material-ui/core/ListItemAvatar';
import GroupIcon from "@material-ui/icons/Group";
import Avatar from "../avatar/Avatar"
import { getAvatarURL } from "../../api/api.js";

import "./Personnel.css";

const Personnel = () => {
  const { state, dispatch } = useContext(AppContext);
  const { csrf, userProfile } = state;
  const id =
    userProfile && userProfile.memberProfile
      ? userProfile.memberProfile.id
      : undefined;
  const [personnel, setPersonnel] = useState();
  const [checkins, setCheckins] = useState([]);

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
        const tmpCheckins = [];
        for (const person of personnel) {
          let res = await getCheckinByMemberId(person.id, csrf);
          let newCheckins = [];
          let data =
            res && res.payload && res.payload.status === 200
              ? res.payload.data
              : null;
          if (data && data.length > 0 && !res.error) {
            data.sort((a, b) => (a.checkInDate < b.checkInDate ? 1 : -1));
            newCheckins = data;
          }
          let personWithCheckin = Object.assign({}, person);
          personWithCheckin.checkins = newCheckins;
          tmpCheckins.push(personWithCheckin);
        }
        setCheckins(tmpCheckins);
      }
    }
    if (csrf) {
      updateCheckins();
    }
  }, [csrf, personnel]);

  // Create entry of member and their last checkin
  function createEntry(person, checkins, keyInput) {
    let key = keyInput ? keyInput : undefined;
    let name = "Team Member";
    let workEmail = "";
    let lastCheckInDate = "Unknown";
    if (checkins && checkins.length) {
      const lastCheckin = checkins[checkins.length - 1];
      const [year, month, day, hour, minute] = lastCheckin.checkInDate;
      lastCheckInDate = new Date(year, month - 1, day, hour, minute, 0).toLocaleDateString();
    }

    if (person) {
      let id = person.id ? person.id : null;
      name = person.name ? person.name : id ? id : name;
      workEmail = person.workEmail;
      key = id && !key ? `${id}Personnel` : key;
    }

    return (
      <ListItem key={key} button
          onClick={() => {
            dispatch({ type: UPDATE_SELECTED_PROFILE, payload: person });
          }}
      >
        <ListItemAvatar>
          <Avatar
            alt={name}
            src={getAvatarURL(workEmail)}
          />
        </ListItemAvatar>
        <ListItemText primary={name} secondary={lastCheckInDate}/>
      </ListItem>
    );
  }

  // Create the entries for the personnel container
  const createPersonnelEntries = () => {
    if (checkins && checkins.length > 0) {
      return checkins.map((person) =>
        createEntry(person, person.checkins, null)
      );
    } else if (personnel && personnel.length > 0) {
      return personnel.map((person) => createEntry(person, null, null));
    } else {
      let fake = Array(3);
      for (let i = 0; i < fake.length; i++) {
        fake[i] = createEntry(null, null, `${i + 1}Personnel`);
      }
      return fake;
    }
  };

  return (
    <Card>
      <CardHeader avatar={<GroupIcon />} title="Development Partners" />
        <List dense>
          {createPersonnelEntries()}
        </List>
    </Card>
  );
};

export default Personnel;
