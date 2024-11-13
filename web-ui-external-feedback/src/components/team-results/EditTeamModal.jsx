import React, { useContext, useState, useEffect, useCallback } from 'react';

import { AppContext } from '../../context/AppContext';
import {
  selectCurrentUser,
  selectCurrentMembers
} from '../../context/selectors';

import { Button } from '@mui/material';
import Modal from '@mui/material/Modal';
import TextField from '@mui/material/TextField';
import Checkbox from '@mui/material/Checkbox';
import Autocomplete from '@mui/material/Autocomplete';
import './EditTeamModal.css';

const EditTeamModal = ({ team = {}, open, onSave, onClose, headerText }) => {
  const { state } = useContext(AppContext);
  const currentMembers = selectCurrentMembers(state);
  const currentUser = selectCurrentUser(state);
  const [editedTeam, setTeam] = useState(team);
  const [teamMemberOptions, setTeamMemberOptions] = useState([]);
  const teamMembers = team?.teamMembers;

  const findExistingMember = useCallback(
    member =>
      teamMembers?.find(current => {
        return current.memberId === member.memberId;
      }),
    [teamMembers]
  );

  useEffect(() => {
    if (open && team.id !== editedTeam.id) setTeam(team);
  }, [open, team, editedTeam]);

  useEffect(() => {
    if (
      currentUser?.id &&
      (editedTeam.teamMembers === undefined ||
        editedTeam.teamMembers === null ||
        editedTeam.teamMembers.length === 0 ||
        editedTeam.teamMembers.filter(member => member.lead === true).length ===
          0)
    ) {
      let teamMembers = [
        {
          id: findExistingMember({ memberId: currentUser.id })?.id,
          memberId: currentUser.id,
          name: `${currentUser.firstName} ${currentUser.lastName}`,
          teamId: editedTeam.id,
          lead: true
        }
      ];
      // Keep current members if all leads are removed
      if (editedTeam && editedTeam.teamMembers) {
        const extantMembers = editedTeam.teamMembers.filter(
          member => member.lead === false
        );
        teamMembers = teamMembers.concat(extantMembers);
      }

      setTeam({
        ...editedTeam,
        teamMembers: teamMembers
      });
    }
  }, [editedTeam, currentUser, findExistingMember]);

  // Sets the options for team members
  useEffect(() => {
    if (!editedTeam || !editedTeam.teamMembers || !currentMembers) return;
    let teamMemberNames = editedTeam.teamMembers.map(
      teamMember => teamMember.name
    );
    setTeamMemberOptions(
      currentMembers.filter(member => !teamMemberNames.includes(member.name))
    );
  }, [currentMembers, editedTeam]);

  const onLeadsChange = (event, leads) => {
    let extantMembers =
      editedTeam && editedTeam.teamMembers
        ? editedTeam.teamMembers.filter(teamMember => !teamMember.lead)
        : [];
    leads = leads.map(lead => ({
      id: lead.memberId ? lead.id : undefined,
      name: lead.name,
      memberId: lead.memberId ? lead.memberId : lead.id,
      teamId: editedTeam.id,
      lead: true
    }));

    leads.forEach(lead => {
      extantMembers = extantMembers.filter(
        member => member.memberId !== lead.memberId
      );
    });

    extantMembers = [...new Set(extantMembers)];
    leads = [...new Set(leads)];

    setTeam({
      ...editedTeam,
      teamMembers: [...extantMembers, ...leads].map(member => {
        const existing = findExistingMember(member);
        if (existing) {
          return { ...member, id: existing.id };
        } else {
          return member;
        }
      })
    });
  };

  const onTeamMembersChange = (event, regularMembers) => {
    let extantLeads =
      editedTeam && editedTeam.teamMembers
        ? editedTeam.teamMembers.filter(teamMember => teamMember.lead)
        : [];

    regularMembers = regularMembers.map(member => ({
      id: member.memberId ? member.id : undefined,
      name: member.name,
      memberId: member.memberId ? member.memberId : member.id,
      teamId: editedTeam.id,
      lead: false
    }));

    regularMembers.forEach(teamMember => {
      extantLeads = extantLeads.filter(
        lead => lead.memberId !== teamMember.memberId
      );
    });

    extantLeads = [...new Set(extantLeads)];
    regularMembers = [...new Set(regularMembers)];

    setTeam({
      ...editedTeam,
      teamMembers: [...extantLeads, ...regularMembers].map(member => {
        const existing = findExistingMember(member);
        if (existing) {
          return { ...member, id: existing.id };
        } else {
          return member;
        }
      })
    });
  };

  const readyToEdit = team => {
    let numLeads = 0;
    if (team && team.teamMembers) {
      numLeads = team.teamMembers.filter(teamMember => teamMember.lead).length;
    }
    return team.name && numLeads > 0;
  };

  const close = () => {
    onClose();
    setTeam({});
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
          value={editedTeam.name ? editedTeam.name : ''}
          onChange={e => setTeam({ ...editedTeam, name: e.target.value })}
        />
        <Checkbox
            id="team-active-input"
            label="Active"
            variant="outlined"
            className="halfWidth"
            checked={editedTeam.active ? editedTeam.active : false}
            onChange={e => setTeam({ ...editedTeam, active: e.target.checked })}
        /> Active
        <TextField
          id="team-description-input"
          label="Description"
          className="fullWidth"
          placeholder="What do they do?"
          value={editedTeam.description ? editedTeam.description : ''}
          onChange={e =>
            setTeam({ ...editedTeam, description: e.target.value })
          }
        />
        <Autocomplete
          id="teamLeadSelect"
          multiple
          isOptionEqualToValue={(option, value) => {
            return value ? value.memberId === option.memberId : false;
          }}
          options={teamMemberOptions}
          required
          value={
            editedTeam.teamMembers
              ? editedTeam.teamMembers.filter(teamMember => teamMember.lead)
              : []
          }
          onChange={onLeadsChange}
          getOptionLabel={member => member.name}
          renderInput={params => (
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
          isOptionEqualToValue={(option, value) => {
            return value ? value.memberId === option.memberId : false;
          }}
          options={teamMemberOptions}
          value={
            editedTeam.teamMembers
              ? editedTeam.teamMembers.filter(teamMember => !teamMember.lead)
              : []
          }
          onChange={onTeamMembersChange}
          getOptionLabel={member => member.name}
          renderInput={params => (
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
              close();
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
