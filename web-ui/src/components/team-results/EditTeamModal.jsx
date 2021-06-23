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

  // Sets the current user as the lead when creating a new team
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

  // Sets the options for team members
  useEffect(() => {
    if (!editedTeam || !editedTeam.teamMembers || !currentMembers) return;

    let memberOptions = currentMembers.map((member) => {
      return {
        memberId: member.id,
        name: member.name,
        firstName: member.firstName,
        lastName: member.lastName
      }
    });

    let teamMemberNames = editedTeam.teamMembers.map(
      (teamMember) => teamMember.name
    );
    memberOptions = memberOptions.filter((member) => !teamMemberNames.includes(member.name));
    setTeamMemberOptions(memberOptions);
  }, [currentMembers, editedTeam]);

  const onLeadsChange = (event, leads) => {
    let extantMembers =
      editedTeam && editedTeam.teamMembers
        ? editedTeam.teamMembers.filter((teamMember) => !teamMember.lead)
        : [];
    leads.forEach((lead) => (lead.lead = true));
    leads.forEach((lead) => {
      extantMembers = extantMembers.filter(
        (member) => member.memberId !== lead.memberId
      );
    });

    extantMembers = [...new Set(extantMembers)];
    leads = [...new Set(leads)];

    setTeam({
      ...editedTeam,
      teamMembers: [...extantMembers, ...leads],
    });
  };

  const onTeamMembersChange = (event, regularMembers) => {

    let extantLeads =
      editedTeam && editedTeam.teamMembers
        ? editedTeam.teamMembers.filter((teamMember) => teamMember.lead)
        : [];

    regularMembers.forEach((teamMember) => (teamMember.lead = false));
    regularMembers.forEach((teamMember) => {
      extantLeads = extantLeads.filter(
        (lead) => lead.memberId !== teamMember.memberId
      );
    });

    extantLeads = [...new Set(extantLeads)];
    regularMembers = [...new Set(regularMembers)];

    setTeam({
      ...editedTeam,
      teamMembers: [...extantLeads, ...regularMembers],
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
          getOptionSelected={(option, value) => {
            return value ? value.memberId === option.memberId : false;
          }}
          options={teamMemberOptions}
          required
          value={
            editedTeam.teamMembers
              ? editedTeam.teamMembers.filter((teamMember) => teamMember.lead)
              : []
          }
          onChange={onLeadsChange}
          getOptionLabel={(member) => member.name}
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
          getOptionSelected={(option, value) => {
            return value ? value.memberId === option.memberId : false;
          }}
          options={teamMemberOptions}
          value={
            editedTeam.teamMembers
              ? editedTeam.teamMembers.filter((teamMember) => !teamMember.lead)
              : []
          }
          filterSelectedOptions
          onChange={onTeamMembersChange}
          getOptionLabel={(member) => member.name}
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
