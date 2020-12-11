import React, { useContext, useState } from "react";

import AddTeamModal from "./EditTeamModal";
import { createTeam } from "../../api/team";
import { AppContext, ADD_TEAM } from "../../context/AppContext";

import { Button } from "@material-ui/core";
import GroupIcon from "@material-ui/icons/Group";

import "./TeamResults.css";

const displayName = "TeamsActions";

const TeamsActions = () => {
  const { state, dispatch } = useContext(AppContext);
  const [open, setOpen] = useState(false);
  
  const { csrf } = state;

  const handleOpen = () => setOpen(true);

  const handleClose = () => setOpen(false);

  return (
    <div className="team-actions">
      <Button startIcon={<GroupIcon />} onClick={handleOpen}>
        Add Team
      </Button>
      <AddTeamModal
        open={open}
        onClose={handleClose}
        onSave={async (team) => {
          if (csrf) {
            let res = await createTeam(team, csrf);
            let data =
              res.payload && res.payload.data && !res.error
                ? res.payload.data
                : null;
            if (data) {
              dispatch({ type: ADD_TEAM, payload: data });
            }
            handleClose();
          }
        }}
      />
    </div>
  );
};

TeamsActions.displayName = displayName;

export default TeamsActions;
