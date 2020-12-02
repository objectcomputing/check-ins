import React, { useContext, useEffect, useState } from "react";

import { getSkillMembers } from "../../api/memberskill";
import { updateSkill } from "../../api/skill";
import {
  AppContext,
  selectProfileMap,
  UPDATE_SKILL,
} from "../../context/AppContext";

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
} from "@material-ui/core";

import "./PendingSkillsModal.css";

const PendingSkillsCard = ({ pendingSkill }) => {
  const { state, dispatch } = useContext(AppContext);
  const { csrf } = state;

  const [members, setMembers] = useState([]);

  const [open, setOpen] = useState(false);
  const [editedSkill, setEditedSkill] = useState(pendingSkill);
  const { description, id, name } = editedSkill;

  const handleOpen = () => {
    setOpen(true);
    setEditedSkill(pendingSkill);
  };
  const handleClose = () => {
    setOpen(false);
  };

  const editSkill = async () => {
    if (editedSkill.name && editedSkill.id) {
      const res = await updateSkill(editedSkill);
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
    if (editedSkill.name && editedSkill.id) {
      const res = await updateSkill({ ...editedSkill, pending: false });
      const data =
        res && res.payload && res.payload.data ? res.payload.data : null;
      if (!data) {
        return;
      }
      dispatch({ type: UPDATE_SKILL, payload: data });
      handleClose();
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
        avatar={<Avatar src={position.imageURL}></Avatar>}
        label={position.name}
      ></Chip>
    );
  };

  const submittedBy = (members) => {
    const [first, second, ...rest] = members;
    const firstProfile = selectProfileMap(state)[first];
    if (second) {
      const secondProfile = selectProfileMap(state)[second];
      return (
        <div>
          Submitted By: {chip(firstProfile)} {chip(secondProfile)}
          {rest && ` and ${rest.length} others`}.
        </div>
      );
    } else return firstProfile && <div>Submitted by: {chip(firstProfile)}</div>;
  };

  return (
    <Card className="pending-skills-card">
      <CardHeader subheader={members && submittedBy(members)} title={name} />
      <CardContent>
        <div>
          Description: {description}
          <CardActions>
            <Button onClick={acceptSkill}>Accept</Button>
            <Button onClick={handleOpen}>Edit</Button>
            <Modal open={open} onClose={handleClose}>
              <div className="PendingSkillsModal">
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
        </div>
      </CardContent>
    </Card>
  );
};

export default PendingSkillsCard;
