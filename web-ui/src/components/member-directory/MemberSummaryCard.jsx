import React, { useContext, useState } from "react";

import MemberModal from "./MemberModal";
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
import Dialog from '@material-ui/core/Dialog';
import DialogTitle from '@material-ui/core/Dialog';
import DialogContent from '@material-ui/core/Dialog';
import DialogContentText from '@material-ui/core/Dialog';
import DialogActions from '@material-ui/core/Dialog';
import Button from '@material-ui/core/Button';
import {deleteMember} from "../../api/member.js";
import {DELETE_MEMBER_PROFILE} from "../../context/actions.js";
import {UPDATE_TOAST} from "../../context/actions.js";

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

  const [openDelete, setOpenDelete] = useState(false);
  const handleOpenDeleteConfirmation = () => setOpenDelete(true);

  const handleClose = () => setOpen(false);
  const handleCloseDeleteConfirmation = () => setOpenDelete(false);


  const options =
      isAdmin ? ["Edit", "Terminate", "Delete"] : ["Edit"];

  const handleAction = (e, index) => {
    if (index === 0){
        handleOpen();
    } else if(index === 2){
        handleOpenDeleteConfirmation();
    }

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
      <Dialog
        open={openDelete}
        onClose={handleCloseDeleteConfirmation}
        aria-labelledby="alert-dialog-title"
        aria-describedby="alert-dialog-description">
            <DialogTitle id="alert-dialog-title">{"Delete Member's data?"}</DialogTitle>
                <DialogContent>
                    <DialogContentText id="alert-dialog-description">
                        Are you sure you want to delete the member's data?
                    </DialogContentText>
                </DialogContent>
            <DialogActions>
                <Button onClick={handleCloseDeleteConfirmation} color="primary">
                    Cancel
                </Button>
                <Button
                    onClick={handleCloseDeleteConfirmation} color="primary" autoFocus
                    onSave={async (id) => {
                    let res = await deleteMember(id, csrf)
                    if (res && res.payload && res.payload.status === 200) {
                            dispatch({ type: DELETE_MEMBER_PROFILE, payload: id });
                            window.snackDispatch({
                                type: UPDATE_TOAST,
                                payload: {
                                    severity: "success",
                                    toast: "Member deleted",
                                }
                            })
                    handleCloseDeleteConfirmation();
                    }
                }}
                >
                    Yes
                </Button>
            </DialogActions>
      </Dialog>
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
            </CardActions>
          )}
        </Card>
      </Box>
  );
};

export default MemberSummaryCard;
