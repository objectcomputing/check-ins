import React, { useContext, useEffect, useState } from "react";
import { useHistory } from "react-router-dom";
import { getMembersByPDL } from "../../api/member";
import { getCheckinByMemberId, getCheckinByPdlId } from "../../api/checkins";
import { AppContext } from "../../context/AppContext";
import { UPDATE_CHECKINS } from "../../context/actions";
import {
  selectCurrentUserId,
  selectMostRecentCheckin,
  selectCsrfToken,
  selectProfile,
} from "../../context/selectors";
import Card from "@mui/material/Card";
import CardHeader from "@mui/material/CardHeader";
import Divider from "@mui/material/Divider";
import List from "@mui/material/List";
import ListItem from "@mui/material/ListItem";
import ListItemText from "@mui/material/ListItemText";
import ListItemAvatar from "@mui/material/ListItemAvatar";
import GroupIcon from "@mui/icons-material/Group";
import Avatar from "../avatar/Avatar";
import { getAvatarURL } from "../../api/api.js";

import "./Personnel.css";

const Personnel = () => {
  const { state, dispatch } = useContext(AppContext);
  const history = useHistory();
  const csrf = selectCsrfToken(state);
  const id = selectCurrentUserId(state);
  const [pastCheckins, setPastCheckins] = useState();
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

  // Get former personnel
  useEffect(() => {
    async function updatePastCheckins() {
      if (id) {
        let res = await getCheckinByPdlId(id, csrf);
        let data =
          res.payload.data && res.payload.status === 200 && !res.error
            ? res.payload.data
            : null;
        if (data) {
          setPastCheckins(data);
        }
      }
    }
    if (csrf) {
      updatePastCheckins();
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
  const createFeedbackRequestLink = (memberId) => (
    <span
      className="feedback-link"
      onClick={(e) => {
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
    let name = "Team Member";
    let workEmail = "";

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
      return personnel.map((person) =>
        createEntry(person, selectMostRecentCheckin(state, person.id), null)
      );
    } else {
      return [];
    }
  };

  // Create entries for open checkins for former personnel
  const pastCheckinsEntries = () => {
    if (pastCheckins && pastCheckins.length > 0) {
      const personnelIds = personnel.map((person) => person.id);
      const pastPersonnelIds = pastCheckins
        .filter((checkins) => !personnelIds.includes(checkins.teamMemberId))
        .reduce((pastIds, checkins) => {
          if (!personnelIds.includes(checkins.teamMemberId)) {
            pastIds.push(checkins.teamMemberId);
          }
          return pastIds;
        }, []);
      return pastPersonnelIds.map((memberId) => {
        const person = selectProfile(state, memberId);
        return createEntry(
          person,
          selectMostRecentCheckin(state, memberId),
          null
        );
      });
    } else {
      return [];
    }
  };

  return (
    <Card>
      <CardHeader avatar={<GroupIcon />} title="Development Partners" />
      <List dense>{createPersonnelEntries()}</List>
      <Divider variant="middle" />
      <CardHeader avatar={<GroupIcon />} title="Former Partners" />
      <List dense>{pastCheckinsEntries()}</List>
    </Card>
  );
};

export default Personnel;
