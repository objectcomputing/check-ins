import React, { useState } from "react";
import { Button } from "@material-ui/core";
import GroupIcon from "@material-ui/icons/Group";
import AddTeamModal from "./EditTeamModal";
import "./TeamResults.css";

const displayName = "TeamsActions";

const TeamsActions = () => {
  const [open, setOpen] = useState(false);

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
        onSave={(team) => {
          console.log(JSON.stringify(team));
          handleClose();
        }}
      />
    </div>
  );
};

TeamsActions.displayName = displayName;

export default TeamsActions;
