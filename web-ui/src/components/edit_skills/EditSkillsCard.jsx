import React, { useContext, useEffect, useState } from "react";

import {
  deleteMemberSkill,
  getSkillMembers,
  getMemberSkills,
} from "../../api/memberskill";
import { removeSkill, updateSkill } from "../../api/skill";
import { AppContext } from "../../context/AppContext";
import { selectProfileMap } from "../../context/selectors";
import {
  DELETE_SKILL,
  UPDATE_SKILL,
  UPDATE_MEMBER_SKILLS,
} from "../../context/actions";
import { getAvatarURL } from "../../api/api.js";

import {
  Avatar,
  Button,
  Chip,
  Card,
  CardActions,
  CardHeader,
  CardContent,
  Modal,
  TextField,
} from "@mui/material";

import "./EditSkills.css";

const EditSkillsCard = ({ skill }) => {
  const { state, dispatch } = useContext(AppContext);
  const { csrf } = state;

  const [members, setMembers] = useState([]);

  const [open, setOpen] = useState(false);
  const [editedSkill, setEditedSkill] = useState(skill);
  const { description, id, name } = editedSkill;

  const handleOpen = () => {
    setOpen(true);
    setEditedSkill(skill);
  };
  const handleClose = () => {
    setOpen(false);
  };

  const editSkill = async () => {
    if (name && id && csrf) {
      const res = await updateSkill(editedSkill, csrf);
      const data =
        res && res.payload && res.payload.data ? res.payload.data : null;
      if (!data) {
        return;
      }
      dispatch({ type: UPDATE_SKILL, payload: data });
      setEditedSkill(data);
      handleClose();
    }
  };

  const acceptSkill = async () => {
    if (name && id && csrf) {
      const res = await updateSkill({ ...editedSkill, pending: false }, csrf);
      const data =
        res && res.payload && res.payload.data ? res.payload.data : null;
      if (!data) {
        return;
      }
      dispatch({ type: UPDATE_SKILL, payload: data });
      handleClose();
    }
  };

  const setExtraneous = async () => {
    if (name && id && csrf) {
      const res = await updateSkill(
        { ...editedSkill, pending: false, extraneous: true },
        csrf
      );
      const data =
        res && res.payload && res.payload.data ? res.payload.data : null;
      if (!data) {
        return;
      }
      dispatch({ type: UPDATE_SKILL, payload: data });
      handleClose();
    }
  };

  const deleteSkill = async () => {
    if (name && id && csrf) {
      const memberSkillsToDelete = await getSkillMembers(id, csrf);
      if (memberSkillsToDelete) {
        //remove all memberSkills associated with this skill first
        await Promise.all(
          memberSkillsToDelete.payload.data.map(
            async (member) => await deleteMemberSkill(member.id, csrf)
          )
        );
      }
      await removeSkill(id, csrf);
      dispatch({ type: DELETE_SKILL, payload: id });
      const result = await getMemberSkills(csrf);
      const memberSkills =
        result && result.payload && result.payload.data
          ? result.payload.data
          : null;
      if (memberSkills) {
        dispatch({ type: UPDATE_MEMBER_SKILLS, payload: memberSkills });
      }
    }
  };

  useEffect(() => {
    const getMembers = async () => {
      let res = await getSkillMembers(id, csrf);
      if (res && res.payload && res.payload.data) {
        const memberIds = res.payload.data.map((m) => m.memberid);
        setMembers(memberIds);
      }
    };
    if (csrf) {
      getMembers();
    }
  }, [csrf, id]);

  const chip = (position) => {
    return (
      <Chip
        avatar={<Avatar src={getAvatarURL(position?.workEmail)}></Avatar>}
        label={position?.name}
      ></Chip>
    );
  };

  const submittedBy = (members) => {
    const [first, second, ...rest] = members;
    const firstProfile = selectProfileMap(state)[first];
    if (second) {
      const secondProfile = selectProfileMap(state)[second];
      return rest.length ? (
        <div data-testid="skill-submitted-by">
          Submitted By: {chip(firstProfile)} {chip(secondProfile)}
          {rest && ` and ${rest.length} others`}.
        </div>
      ) : (
        <div data-testid="skill-submitted-by">
          Submitted By: {chip(firstProfile)} {chip(secondProfile)}
        </div>
      );
    } else
      return firstProfile ? (
        <div data-testid="skill-submitted-by">Submitted by: {chip(firstProfile)}</div>
      ) : (
        <div data-testid="skill-submitted-by">Submitted by: Unknown</div>
      );
  };

  return (
    <Card className="pending-skills-card">
      <CardHeader subheader={description} title={name} />
      <CardContent>
        <div>{members && submittedBy(members)}</div>
      </CardContent>
      <CardActions>
        {skill.pending && <Button onClick={acceptSkill}>Accept</Button>}
        <Button onClick={handleOpen}>Edit</Button>
        <Button onClick={deleteSkill}>Delete</Button>
        {skill.pending && (
          <Button onClick={setExtraneous}>Mark Extraneous</Button>
        )}
        <Modal open={open} onClose={handleClose}>
          <div className="pending-skills-modal">
            <TextField
              className="halfWidth"
              label="Name"
              onChange={(e) =>
                setEditedSkill({ ...editedSkill, name: e.target.value })
              }
              value={editedSkill ? editedSkill.name : ""}
              variant="outlined"
            />
            <TextField
              className="halfWidth"
              label="Description"
              multiline
              onChange={(e) =>
                setEditedSkill({
                  ...editedSkill,
                  description: e.target.value,
                })
              }
              value={editedSkill ? editedSkill.description : ""}
              variant="outlined"
            />
            <Button onClick={handleClose}>Cancel</Button>
            <Button onClick={editSkill}>Save</Button>
          </div>
        </Modal>
      </CardActions>
    </Card>
  );
};

export default EditSkillsCard;
