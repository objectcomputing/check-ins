import React, { useContext, useState } from "react";

import MemberModal from "./MemberModal";
import { AppContext, UPDATE_MEMBER_PROFILES } from "../../context/AppContext";
import { getAvatarURL } from "../../api/api.js";

import { Card, CardActions, CardHeader } from "@material-ui/core";
import Avatar from "@material-ui/core/Avatar";

import "./MemberSummaryCard.css";
import SplitButton from "../split-button/SplitButton";

const MemberSummaryCard = ({ member, index }) => {
  const { state, dispatch } = useContext(AppContext);
  const { memberProfiles, userProfile } = state;
  const isAdmin =
    userProfile && userProfile.role && userProfile.role.includes("ADMIN");
  const { location, name, workEmail, title, supervisorid } = member;
  const [currentMember, setCurrentMember] = useState(member);
  const supervisorProfile = memberProfiles.find((memberProfile) =>
                                      memberProfile.id === supervisorid);

  const [open, setOpen] = useState(false);
  const handleOpen = () => setOpen(true);

  const handleClose = () => setOpen(false);

  const options =
      isAdmin ? ["Edit", "Terminate", "Delete"] : ["Edit"];

  const handleAction = (e, index) =>
      index === 0 ? handleOpen() : handleClose();

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
             <br />
            {supervisorProfile ? supervisorProfile.name : ""}
          </div>
        }
        title={name}
      />
      {isAdmin && (
        <CardActions>
          <SplitButton options={options} onClick={handleAction} />
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
