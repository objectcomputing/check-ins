import React, { useContext, useState, useEffect } from "react";

import { AppContext } from "../../context/AppContext";
import {
  selectCurrentUser,
  selectCurrentMembers,
} from "../../context/selectors";

import { Button } from "@material-ui/core";
import Modal from "@material-ui/core/Modal";
import TextField from "@material-ui/core/TextField";
import Autocomplete from "@material-ui/lab/Autocomplete";
import "./EditTeamModal.css";

const EditTeamModal = ({ team = {}, open, onSave, onClose, headerText }) => {
  const { state } = useContext(AppContext);
  const currentMembers = selectCurrentMembers(state);
  const currentUser = selectCurrentUser(state);
  const [editedTeam, setTeam] = useState(team);
  const [teamMemberOptions, setTeamMemberOptions] = useState([]);

  useEffect(() => {
    if (
      currentUser?.id &&
      (editedTeam.teamMembers === undefined ||
        editedTeam.teamMembers.length === 0)
    ) {
      setTeam({
        ...editedTeam,
        teamMembers: [
          {
            memberId: currentUser.id,
            name: `${currentUser.firstName} ${currentUser.lastName}`,
            firstName: currentUser.firstName,
            lastName: currentUser.lastName,
            lead: true,
          },
        ],
      });
    }
  }, [editedTeam, currentUser]);

  useEffect(() => {
    if (!editedTeam || !editedTeam.teamMembers || !currentMembers) return;
    let teamMemberNames = editedTeam.teamMembers.map(
      (teamMember) => teamMember.name
    );
    setTeamMemberOptions(
      currentMembers.filter((member) => !teamMemberNames.includes(member.name))
    );
  }, [currentMembers, editedTeam]);

  const onLeadsChange = (event, newValue) => {
    let extantMembers =
      editedTeam && editedTeam.teamMembers
        ? editedTeam.teamMembers.filter((teamMember) => !teamMember.lead)
        : [];
    newValue.forEach((lead) => (lead.lead = true));
    newValue.forEach((newLead) => {
      extantMembers = extantMembers.filter(
        (member) => member.memberId !== newLead.id && member.id !== newLead.id
      );
    });
    extantMembers = [...new Set(extantMembers)];
    newValue = [...new Set(newValue)];
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
    newValue.forEach((teamMember) => (teamMember.lead = false));
    newValue.forEach((newMember) => {
      extantLeads = extantLeads.filter(
        (lead) => lead.memberId !== newMember.id && lead.id !== newMember.id
      );
    });
    extantLeads = [...new Set(extantLeads)];
    newValue = [...new Set(newValue)];
    setTeam({
      ...editedTeam,
      teamMembers: [...extantLeads, ...newValue],
    });
  };

  const readyToEdit = (team) => {
    let numLeads = 0;
    if (team && team.teamMembers) {
      numLeads = team.teamMembers.filter((teamMember) => teamMember.lead)
        .length;
    }
    return team.name && numLeads > 0;
  };

  const close = () => {
    onClose();
    setTeam(team);
  };

  return (
    <Modal open={open} onClose={close} aria-labelledby="edit-team-modal-title">
      <div className="EditTeamModal">
        <h2>{headerText}</h2>
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
          id="teamLeadSelect"
          multiple
          options={teamMemberOptions}
          required
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
              label="Team Leads *"
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
          <Button onClick={close} color="secondary">
            Cancel
          </Button>
          <Button
            disabled={!readyToEdit(editedTeam)}
            onClick={() => {
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
