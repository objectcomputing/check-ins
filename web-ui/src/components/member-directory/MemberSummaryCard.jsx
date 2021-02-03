import React, { useContext, useState } from "react";

import MemberModal from "./MemberModal";
import { AppContext, UPDATE_MEMBER_PROFILES } from "../../context/AppContext";
import { getAvatarURL } from "../../api/api.js";

import { Card, CardActions, CardHeader } from "@material-ui/core";
import Avatar from "@material-ui/core/Avatar";

import "./MemberSummaryCard.css";
import SplitButton from "../split-button/SplitButton";

import Typography from "@material-ui/core/Typography";
import CardContent from "@material-ui/core/CardContent";
import Container from "@material-ui/core/Container";
import Box from "@material-ui/core/Box";

const MemberSummaryCard = ({ member, index }) => {
  const { state, dispatch } = useContext(AppContext);
  const { memberProfiles, userProfile } = state;
  const isAdmin =
    userProfile && userProfile.role && userProfile.role.includes("ADMIN");
  const { location, name, workEmail, title, supervisorid } = member;
  const [currentMember, setCurrentMember] = useState(member);
  const supervisorProfile = memberProfiles ? memberProfiles.find((memberProfile) =>
                                      memberProfile.id === supervisorid) : null;

  const [open, setOpen] = useState(false);
  const handleOpen = () => setOpen(true);

  const handleClose = () => setOpen(false);

  const options =
      isAdmin ? ["Edit", "Terminate", "Delete"] : ["Edit"];

  const handleAction = (e, index) =>
      index === 0 ? handleOpen() : handleClose();

  return (
    <Box display="flex" flexWrap="wrap">
      <Card className={"member-card"}>
        <Container fixed className={"info-container"}>
          <CardHeader
            title={
              <Typography variant="h5" component="h2">
                {name}
              </Typography>
            }
            subheader={<Typography component="h3">{title}</Typography>}
            disableTypography
            avatar={
              <Avatar
                className={"large"}
                src={getAvatarURL(workEmail)}
              />
            }
          ></CardHeader>
        </Container>
          <CardContent>
            <Container fixed className={"info-container"}>
              <Typography variant="body2" color="textSecondary" component="p">
                <a href={`mailto:${workEmail}`}>
                  {workEmail}
                </a>
                <br />
                Location: {location}
                <br />
                {supervisorProfile ? "Supervisor: " + supervisorProfile.name : ""}
                <br />
              </Typography>
            </Container>
          </CardContent>
            {isAdmin && (
            <CardActions>
              <SplitButton className = "split-button" options={options} onClick={handleAction} />
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
      </Box>
  );
};

export default MemberSummaryCard;
