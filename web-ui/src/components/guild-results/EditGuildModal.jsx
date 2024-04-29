import React, { useContext, useState, useEffect, useCallback } from 'react';

import { AppContext } from '../../context/AppContext';
import {
  selectCurrentUser,
  selectCurrentMembers
} from '../../context/selectors';

import { Button, Modal, TextField } from '@mui/material';
import Autocomplete from '@mui/material/Autocomplete';
import './EditGuildModal.css';

const EditGuildModal = ({ guild = {}, open, onSave, onClose, headerText }) => {
  const { state } = useContext(AppContext);
  const currentUser = selectCurrentUser(state);
  const [editedGuild, setGuild] = useState(guild);
  const [guildMemberOptions, setGuildMemberOptions] = useState([]);
  const currentMembers = selectCurrentMembers(state);
  const guildMembers = guild?.guildMembers;

  const findExistingMember = useCallback(
    member =>
      guildMembers?.find(current => current.memberId === member.memberId),
    [guildMembers]
  );

  useEffect(() => {
    if (open && guild.id !== editedGuild.id) setGuild(guild);
  }, [open, guild, editedGuild]);

  useEffect(() => {
    if (
      currentUser?.id &&
      (editedGuild.guildMembers === undefined ||
        editedGuild.guildMembers.length === 0 ||
        editedGuild.guildMembers.filter(member => member.lead === true)
          .length === 0)
    ) {
      setGuild({
        ...editedGuild,
        guildMembers: [
          ...new Set(
            editedGuild?.guildMembers?.filter(
              member =>
                member.lead === false && member.memberId !== currentUser.id
            )
          ),
          {
            id: findExistingMember({ memberId: currentUser.id })?.id,
            name: `${currentUser.firstName} ${currentUser.lastName}`,
            memberId: currentUser.id,
            guildId: editedGuild.id,
            lead: true
          }
        ]
      });
    }
  }, [editedGuild, currentUser, findExistingMember]);

  useEffect(() => {
    if (!editedGuild || !editedGuild.guildMembers || !currentMembers) return;
    let guildMemberNames = editedGuild.guildMembers.map(
      guildMember => guildMember.name
    );
    setGuildMemberOptions(
      currentMembers.filter(member => !guildMemberNames.includes(member.name))
    );
  }, [currentMembers, editedGuild]);

  const onLeadsChange = (event, newValue) => {
    let extantMembers =
      editedGuild && editedGuild.guildMembers
        ? editedGuild.guildMembers.filter(guildMember => !guildMember.lead)
        : [];
    newValue = newValue.map(newLead => ({
      id: newLead.memberId ? newLead.id : undefined,
      name: newLead.name,
      memberId: newLead.memberId ? newLead.memberId : newLead.id,
      guildId: editedGuild.id,
      lead: true
    }));
    newValue.forEach(newLead => {
      extantMembers = extantMembers.filter(
        member => member.memberId !== newLead.memberId
      );
    });
    extantMembers = [...new Set(extantMembers)];
    newValue = [...new Set(newValue)];
    setGuild({
      ...editedGuild,
      guildMembers: [...extantMembers, ...newValue].map(member => {
        const existing = findExistingMember(member);
        if (existing) {
          return { ...member, id: existing.id };
        } else {
          return member;
        }
      })
    });
  };

  const onGuildMembersChange = (event, newValue) => {
    let extantLeads =
      editedGuild && editedGuild.guildMembers
        ? editedGuild.guildMembers.filter(guildMember => guildMember.lead)
        : [];
    newValue = newValue.map(newMember => ({
      id: newMember.memberId ? newMember.id : undefined,
      name: newMember.name,
      memberId: newMember.memberId ? newMember.memberId : newMember.id,
      guildId: editedGuild.id,
      lead: false
    }));
    newValue.forEach(newMember => {
      extantLeads = extantLeads.filter(
        lead => lead.memberId !== newMember.memberId
      );
    });
    extantLeads = [...new Set(extantLeads)];
    newValue = [...new Set(newValue)];
    setGuild({
      ...editedGuild,
      guildMembers: [...extantLeads, ...newValue].map(member => {
        const existing = findExistingMember(member);
        if (existing) {
          return { ...member, id: existing.id };
        } else {
          return member;
        }
      })
    });
  };

  const readyToEdit = guild => {
    let numLeads = 0;
    if (guild && guild.guildMembers) {
      numLeads = guild.guildMembers.filter(
        guildMember => guildMember.lead
      ).length;
    }
    return guild.name && numLeads > 0;
  };

  const close = () => {
    onClose();
    setGuild({});
  };

  return (
    <Modal open={open} onClose={close} aria-labelledby="edit-guild-modal-title">
      <div className="EditGuildModal">
        <h2>{headerText}</h2>
        <TextField
          id="guild-name-input"
          label="Guild Name"
          required
          className="halfWidth"
          placeholder="Awesome Guild"
          value={editedGuild.name ? editedGuild.name : ''}
          onChange={e => setGuild({ ...editedGuild, name: e.target.value })}
        />
        <TextField
          id="guild-description-input"
          label="Description"
          className="fullWidth"
          placeholder="What do they do?"
          value={editedGuild.description ? editedGuild.description : ''}
          onChange={e =>
            setGuild({ ...editedGuild, description: e.target.value })
          }
        />
        <TextField
          id="guild-link-input"
          label="Link to Compass Page"
          className="fullWidth"
          placeholder="https://www.compass.objectcomputing.com/guilds/..."
          value={editedGuild.link ? editedGuild.link : ''}
          onChange={e => setGuild({ ...editedGuild, link: e.target.value })}
        />
        <Autocomplete
          id="guildLeadSelect"
          multiple
          options={guildMemberOptions}
          required
          value={
            editedGuild.guildMembers
              ? editedGuild.guildMembers.filter(guildMember => guildMember.lead)
              : []
          }
          isOptionEqualToValue={(option, value) =>
            value && option.id === value.memberId
          }
          onChange={onLeadsChange}
          getOptionLabel={option => option.name}
          renderInput={params => (
            <TextField
              {...params}
              className="fullWidth"
              label="Guild Leads *"
              placeholder="Add a guild lead..."
            />
          )}
        />
        <Autocomplete
          multiple
          options={guildMemberOptions}
          value={
            editedGuild.guildMembers
              ? editedGuild.guildMembers.filter(
                  guildMember => !guildMember.lead
                )
              : []
          }
          onChange={onGuildMembersChange}
          getOptionLabel={option => option.name}
          isOptionEqualToValue={(option, value) =>
            value && option.id === value.memberId
          }
          renderInput={params => (
            <TextField
              {...params}
              className="fullWidth"
              label="Guild Members"
              placeholder="Add a guild member..."
            />
          )}
        />
        <div className="EditGuildModal-actions fullWidth">
          <Button onClick={close} color="secondary">
            Cancel
          </Button>
          <Button
            disabled={!readyToEdit(editedGuild)}
            onClick={() => {
              onSave(editedGuild);
              close();
            }}
            color="primary"
          >
            Save Guild
          </Button>
        </div>
      </div>
    </Modal>
  );
};

export default EditGuildModal;
