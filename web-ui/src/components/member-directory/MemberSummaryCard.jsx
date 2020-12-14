import React, { useContext, useState } from "react";

import MemberModal from "./MemberModal";
import { AppContext, UPDATE_MEMBER_PROFILES } from "../../context/AppContext";
import { getAvatarURL } from "../../api/api.js";

import { Button, Card, CardActions, CardHeader } from "@material-ui/core";
import Avatar from "@material-ui/core/Avatar";

import "./MemberSummaryCard.css";

const MemberSummaryCard = ({ member, index }) => {
  const { state, dispatch } = useContext(AppContext);
  const { memberProfiles, userProfile } = state;
  const isAdmin =
    userProfile && userProfile.role && userProfile.role.includes("ADMIN");
  const { location, name, workEmail, title } = member;
  const [currentMember, setCurrentMember] = useState(member);
  const [open, setOpen] = useState(false);

  const handleOpen = () => setOpen(true);

  const handleClose = () => setOpen(false);

  return (
    <Card className="member-card">
      <CardHeader
        avatar={
          <Avatar
            alt={name}
            className="member-summary-avatar"
            src={getAvatarURL(workEmail)}
            style={{ margin: "0px" }}
          />
        }
        subheader={
          <div>
            {title}
            <br />
            {workEmail}
            <br />
            {location}
          </div>
        }
        title={name}
      />
      {isAdmin && (
        <CardActions>
          <Button onClick={handleOpen}>Edit</Button>
          <Button>Terminate</Button>
          <Button>Delete</Button>
          <MemberModal
            member={currentMember}
            open={open}
            onClose={handleClose}
            onSave={(member) => {
              setCurrentMember(member);
              const copy = [...memberProfiles];
              copy[index] = member;
              dispatch({
                type: UPDATE_MEMBER_PROFILES,
                payload: copy,
              });
              handleClose();
            }}
          />
        </CardActions>
      )}
    </Card>
  );
};

export default MemberSummaryCard;
