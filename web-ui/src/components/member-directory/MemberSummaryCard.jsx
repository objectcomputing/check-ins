import React, { useContext, useState } from "react";

import MemberModal from "./MemberModal";
import TerminateMemberModal from "./TerminateMemberModal";
import { AppContext } from "../../context/AppContext";
import { UPDATE_MEMBER_PROFILES } from "../../context/actions";
import { selectProfileMap } from "../../context/selectors";
import { getAvatarURL } from "../../api/api.js";

import { Card, CardActions, CardHeader } from "@material-ui/core";
import Avatar from "@material-ui/core/Avatar";

import "./MemberSummaryCard.css";
import SplitButton from "../split-button/SplitButton";

import Typography from "@material-ui/core/Typography";
import CardContent from "@material-ui/core/CardContent";
import Container from "@material-ui/core/Container";
import Box from "@material-ui/core/Box";
import {updateMember} from "../../api/member";

const MemberSummaryCard = ({ member, index }) => {
  const { state, dispatch } = useContext(AppContext);
  const { memberProfiles, userProfile, csrf } = state;
  const isAdmin =
    userProfile && userProfile.role && userProfile.role.includes("ADMIN");
  const { location, name, workEmail, title, supervisorid, pdlId } = member;
  const [currentMember, setCurrentMember] = useState(member);
  const supervisorProfile = selectProfileMap(state)[supervisorid];
  const pdlProfile = selectProfileMap(state)[pdlId];

  const [open, setOpen] = useState(false);
  const handleOpen = () => setOpen(true);

  const handleClose = () => setOpen(false);

  const [openTerminal, setOpenTerminal] = useState(false);
  const handleOpenTerminal = () => setOpenTerminal(true);

  const handleCloseTerminal = () => setOpenTerminal(false);

  const options =
      isAdmin ? ["Edit", "Terminate", "Delete"] : ["Edit"];

  const handleAction = (e, index) => {
      if (index === 0)
      handleOpen();
      else if
      (index === 1)
      handleOpenTerminal();
      }

  return (
    <Box display="flex" flexWrap="wrap">
      <Card className={"member-card"}>
          <CardHeader
            title={
              <Typography variant="h5" component="h2">
                {name}
              </Typography>
            }
            subheader={<Typography color="textSecondary" component="h3">{title}</Typography>}
            disableTypography
            avatar={
              <Avatar
                className={"large"}
                src={getAvatarURL(workEmail)}
              />
            }
          />
          <CardContent>
            <Container fixed className={"info-container"}>
              <Typography variant="body2" color="textSecondary" component="p">
                <a href={`mailto:${workEmail}`} target="_blank" rel="noopener noreferrer">
                  {workEmail}
                </a>
                <br />
                Location: {location}<br />
                Supervisor: {supervisorProfile?.name}<br />
                PDL: {pdlProfile?.name}<br />
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
                onSave={async (member) => {
                  setCurrentMember(member);
                  let res = await updateMember(member, csrf);
                  let data =
                      res.payload && res.payload.data && !res.error
                          ? res.payload.data
                          : null;
                  if (data) {
                    const copy = [...memberProfiles];
                    copy[index] = member;
                    dispatch({
                      type: UPDATE_MEMBER_PROFILES,
                      payload: copy,
                    });
                    handleClose();
                  }
                }}
              />
              <TerminateMemberModal
                member={currentMember}
                open={openTerminal}
                onClose={handleCloseTerminal}
                onSave={async (member) => {
                  setCurrentMember(member);
                  let res = await updateMember(member, csrf);
                  let data =
                      res.payload && res.payload.data && !res.error
                          ? res.payload.data
                          : null;
                  if (data) {
                    const copy = [...memberProfiles];
                    copy[index] = member;
                    dispatch({
                      type: UPDATE_MEMBER_PROFILES,
                      payload: copy,
                    });
                    handleCloseTerminal();
                  }
                }}
              />
            </CardActions>
          )}
        </Card>
      </Box>
  );
};

export default MemberSummaryCard;
