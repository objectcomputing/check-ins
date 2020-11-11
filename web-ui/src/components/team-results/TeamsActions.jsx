import React, { useContext, useState } from "react";

import AddTeamModal from "./EditTeamModal";
import {
  AppContext,
  ADD_TEAM,
  UPDATE_TEAM_MEMBERS,
  UPDATE_TOAST,
} from "../../context/AppContext";
import { addTeamMember, createTeam } from "../../api/team";

import Container from "@material-ui/core/Container";
import { Button } from "@material-ui/core";
import GroupIcon from "@material-ui/icons/Group";

import "./TeamResults.css";

const displayName = "TeamsActions";

const TeamsActions = () => {
  const { state, dispatch } = useContext(AppContext);
  const { csrf, teams } = state;

  const [open, setOpen] = useState(false);

  const handleOpen = () => setOpen(true);

  const handleClose = () => setOpen(false);

  const saveTeam = async (team) => {
    if (teams.includes(team.name) || !team.teamLeads || !team.description) {
      dispatch({
        type: UPDATE_TOAST,
        payload: {
          severity: "error",
          toast: "All required fields must be entered",
        },
      });
      return;
    }
    let res = await createTeam(team, csrf);
    if (res && res.payload && res.payload.data) {
      const teamid = res.payload.data.id;
      dispatch({ type: ADD_TEAM, payload: res.payload.data });
      team.teamLeads.forEach(async (member) => {
        let res = await addTeamMember(member, true, teamid, csrf);
        if (res && res.payload && res.payload.data) {
          dispatch({ type: UPDATE_TEAM_MEMBERS, payload: res.payload.data });
        }
      });
      if (team.teamMembers) {
        team.teamMembers.forEach(async (member) => {
          let res = await addTeamMember(member, false, teamid, csrf);
          if (res && res.payload && res.payload.data) {
            dispatch({ type: UPDATE_TEAM_MEMBERS, payload: res.payload.data });
          }
        });
      }
      handleClose();
    }
  };

  return (
    <Container maxWidth="md">
      <div className="team-actions">
        <Button startIcon={<GroupIcon />} onClick={handleOpen}>
          Add Team
        </Button>
        <AddTeamModal open={open} onClose={handleClose} onSave={saveTeam} />
      </div>
    </Container>
  );
};

TeamsActions.displayName = displayName;

export default TeamsActions;
