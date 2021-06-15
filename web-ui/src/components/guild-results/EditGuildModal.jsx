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
import "./EditGuildModal.css";

const EditGuildModal = ({ guild = {}, open, onSave, onClose, headerText }) => {
  const { state } = useContext(AppContext);
  const currentUser = selectCurrentUser(state);
  const [editedGuild, setGuild] = useState(guild);
  const [guildMemberOptions, setGuildMemberOptions] = useState([]);
  const currentMembers = selectCurrentMembers(state);

  useEffect(() => {
    if (
      currentUser?.id &&
      (editedGuild.guildMembers === undefined ||
        editedGuild.guildMembers.length === 0 ||
        editedGuild.guildMembers.filter(member => member.lead === true).length === 0)
    ) {
      setGuild({
        ...editedGuild,
        guildMembers: [...new Set(editedGuild?.guildMembers?.filter(member => member.lead === false && member.memberid !== currentUser.id)),
          {
            name: `${currentUser.firstName} ${currentUser.lastName}`,
            memberid: currentUser.id,
            guildid: editedGuild.id,
            lead: true,
          },
        ],
      });
    }
  }, [editedGuild, currentUser]);

  useEffect(() => {
    if (!editedGuild || !editedGuild.guildMembers || !currentMembers) return;
    let guildMemberNames = editedGuild.guildMembers.map(
      (guildMember) => guildMember.name
    );
    setGuildMemberOptions(
      currentMembers.filter((member) => !guildMemberNames.includes(member.name))
    );
  }, [currentMembers, editedGuild]);

  const onLeadsChange = (event, newValue) => {
    let extantMembers =
      editedGuild && editedGuild.guildMembers
        ? editedGuild.guildMembers.filter((guildMember) => !guildMember.lead)
        : [];
    newValue = newValue.map((newLead) => ({
      id: newLead.memberid ? newLead.id : undefined,
      name: newLead.name,
      memberid: newLead.memberid ? newLead.memberid : newLead.id,
      guildid: editedGuild.id,
      lead: true,
    }));
    newValue.forEach((newLead) => {
      extantMembers = extantMembers.filter(
        (member) => member.memberid !== newLead.memberid
      );
    });
    extantMembers = [...new Set(extantMembers)];
    newValue = [...new Set(newValue)];
    setGuild({
      ...editedGuild,
      guildMembers: [...extantMembers, ...newValue],
    });
  };

  const onGuildMembersChange = (event, newValue) => {
    let extantLeads =
      editedGuild && editedGuild.guildMembers
        ? editedGuild.guildMembers.filter((guildMember) => guildMember.lead)
        : [];
    newValue = newValue.map((newMember) => ({
      id: newMember.memberid ? newMember.id : undefined,
      name: newMember.name,
      memberid: newMember.memberid ? newMember.memberid : newMember.id,
      guildid: editedGuild.id,
      lead: false,
    }));
    newValue.forEach((newMember) => {
      extantLeads = extantLeads.filter(
        (lead) => lead.memberid !== newMember.memberid
      );
    });
    extantLeads = [...new Set(extantLeads)];
    newValue = [...new Set(newValue)];
    setGuild({
      ...editedGuild,
      guildMembers: [...extantLeads, ...newValue],
    });
  };

  const readyToEdit = (guild) => {
    let numLeads = 0;
    if (guild && guild.guildMembers) {
      numLeads = guild.guildMembers.filter((guildMember) => guildMember.lead)
        .length;
    }
    return guild.name && numLeads > 0;
  };

  const close = () => {
    onClose();
    setGuild(guild);
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
          value={editedGuild.name ? editedGuild.name : ""}
          onChange={(e) => setGuild({ ...editedGuild, name: e.target.value })}
        />
        <TextField
          id="guild-description-input"
          label="Description"
          className="fullWidth"
          placeholder="What do they do?"
          value={editedGuild.description ? editedGuild.description : ""}
          onChange={(e) =>
            setGuild({ ...editedGuild, description: e.target.value })
          }
        />
        <Autocomplete
          id="guildLeadSelect"
          multiple
          options={guildMemberOptions}
          required
          value={
            editedGuild.guildMembers
              ? editedGuild.guildMembers.filter(
                  (guildMember) => guildMember.lead
                )
              : []
          }
          onChange={onLeadsChange}
          getOptionLabel={(option) => option.name}
          renderInput={(params) => (
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
                  (guildMember) => !guildMember.lead
                )
              : []
          }
          onChange={onGuildMembersChange}
          getOptionLabel={(option) => option.name}
          getOptionSelected={(option, value) =>
            value ? value.id === option.id : false
          }
          renderInput={(params) => (
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
