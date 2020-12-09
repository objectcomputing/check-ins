import React, { useContext, useState } from "react";

import { AppContext } from "../../context/AppContext";

import { Button } from "@material-ui/core";
import Modal from "@material-ui/core/Modal";
import TextField from "@material-ui/core/TextField";
import Autocomplete from "@material-ui/lab/Autocomplete";
import "./EditTeamModal.css";

const EditTeamModal = ({ team = {}, open, onSave, onClose }) => {
  const { state } = useContext(AppContext);
  const { memberProfiles } = state;
  const [editedTeam, setTeam] = useState(team);
  const teamMemberOptions = memberProfiles;

  const onLeadsChange = (event, newValue) => {
    let extantMembers =
      editedTeam && editedTeam.teamMembers
        ? editedTeam.teamMembers.filter((teamMember) => !teamMember.lead)
        : [];
    newValue.forEach((lead) => (lead.lead = true));
    setTeam({
      ...editedTeam,
      teamMembers: [...extantMembers, ...newValue],
    });
  };

  const onTeamMembersChange = (event, newValue) => {
    let extantLeads =
      editedTeam && editedTeam.teamMembers
        ? editedTeam.teamMembers.filter((teamMember) => teamMember.lead)
        : [];
    newValue.forEach((lead) => (lead.lead = false));
    setTeam({
      ...editedTeam,
      teamMembers: [...extantLeads, ...newValue],
    });
  };

  return (
    <Modal
      open={open}
      onClose={onClose}
      aria-labelledby="edit-team-modal-title"
    >
      <div className="EditTeamModal">
        <h2>Edit your team</h2>
        <TextField
          id="team-name-input"
          label="Team Name"
          required
          className="halfWidth"
          placeholder="Awesome Team"
          value={editedTeam.name ? editedTeam.name : ""}
          onChange={(e) => setTeam({ ...editedTeam, name: e.target.value })}
        />
        <TextField
          id="team-description-input"
          label="Description"
          className="fullWidth"
          placeholder="What do they do?"
          value={editedTeam.description ? editedTeam.description : ""}
          onChange={(e) =>
            setTeam({ ...editedTeam, description: e.target.value })
          }
        />
        <Autocomplete
          multiple
          options={teamMemberOptions}
          value={
            editedTeam.teamMembers
              ? editedTeam.teamMembers.filter((teamMember) => teamMember.lead)
              : []
          }
          onChange={onLeadsChange}
          getOptionLabel={(option) => option.name}
          renderInput={(params) => (
            <TextField
              {...params}
              className="fullWidth"
              label="Team Leads"
              placeholder="Add a team lead..."
            />
          )}
        />
        <Autocomplete
          multiple
          options={teamMemberOptions}
          value={
            editedTeam.teamMembers
              ? editedTeam.teamMembers.filter((teamMember) => !teamMember.lead)
              : []
          }
          onChange={onTeamMembersChange}
          getOptionLabel={(option) => option.name}
          getOptionSelected={(option, value) =>
            value ? value.id === option.id : false
          }
          renderInput={(params) => (
            <TextField
              {...params}
              className="fullWidth"
              label="Team Members"
              placeholder="Add a team member..."
            />
          )}
        />
        <div className="EditTeamModal-actions fullWidth">
          <Button onClick={onClose} color="secondary">
            Cancel
          </Button>
          <Button
            onClick={async () => {
              onSave(editedTeam);
            }}
            color="primary"
          >
            Save Team
          </Button>
        </div>
      </div>
    </Modal>
  );
};

export default EditTeamModal;
