import React, { useContext, useState } from "react";
import { Link } from "react-router-dom";

import MemberModal from "./MemberModal";
import { AppContext } from "../../context/AppContext";
import {
  UPDATE_MEMBER_PROFILES,
} from "../../context/actions";
import { selectProfileMap } from "../../context/selectors";
import { getAvatarURL } from "../../api/api.js";

import { Card, CardActions, CardHeader } from "@material-ui/core";
import Avatar from "@material-ui/core/Avatar";
import PriorityHighIcon from '@material-ui/icons/PriorityHigh';

import "./MemberSummaryCard.css";
import SplitButton from "../split-button/SplitButton";

import { updateMember } from "../../api/member";
import { deleteMember } from "../../api/member.js";
import { DELETE_MEMBER_PROFILE } from "../../context/actions.js";
import { UPDATE_TOAST } from "../../context/actions.js";

import {
  Box,
  Button,
  CardContent,
  Container,
  Dialog,
  DialogActions,
  DialogContent,
  DialogContentText,
  DialogTitle,
  makeStyles,
  Typography,
} from "@material-ui/core";

const useStyles = makeStyles(() => ({
  header: {
    cursor: "pointer",
  },
}));

const MemberSummaryCard = ({ member, index }) => {
  const { state, dispatch } = useContext(AppContext);
  const { memberProfiles, userProfile, csrf } = state;
  const isAdmin =
    userProfile && userProfile.role && userProfile.role.includes("ADMIN");
  const { location, name, workEmail, title, supervisorid, pdlId, terminationDate } = member;
  const memberId = member?.id;
  const supervisorProfile = selectProfileMap(state)[supervisorid];
  const pdlProfile = selectProfileMap(state)[pdlId];

  const classes = useStyles();

  const [open, setOpen] = useState(false);
  const handleOpen = () => setOpen(true);

  const [openDelete, setOpenDelete] = useState(false);
  const handleOpenDeleteConfirmation = () => setOpenDelete(true);

  const handleClose = () => setOpen(false);
  const handleCloseDeleteConfirmation = () => setOpenDelete(false);

  const options = isAdmin ? ["Edit", "Delete"] : ["Edit"];

  const handleAction = (e, index) => {
    if (index === 0) {
      handleOpen();
    } else if (index === 1) {
      handleOpenDeleteConfirmation();
    }
  };

  const handleDeleteMember = async () => {
    let res = await deleteMember(memberId, csrf);
    if (res && res.payload && res.payload.status === 200) {
      dispatch({ type: DELETE_MEMBER_PROFILE, payload: memberId });
      window.snackDispatch({
        type: UPDATE_TOAST,
        payload: {
          severity: "success",
          toast: "Member deleted",
        },
      });
    }
    handleCloseDeleteConfirmation();
  };

  return (
    <Box display="flex" flexWrap="wrap">
      <Card className={"member-card"}>
        <Link
          style={{ color: "black", textDecoration: "none" }}
          to={`/profile/${member.id}`}
        >
          <CardHeader
            className={classes.header}
            title={
              <Typography variant="h5" component="h2" >
                {name}
              </Typography>
            }
            subheader={
              <Typography color="textSecondary" component="h3" >
                {title}
              </Typography>
            }
            disableTypography
            avatar= {!terminationDate?
              <Avatar className={"large"} src={getAvatarURL(workEmail)} /> :
              <Avatar className={"large"} >
                 <PriorityHighIcon />
               </Avatar>
            }
          />
        </Link>
        <CardContent>
          <Container fixed className={"info-container"}>
            <Typography variant="body2" color="textSecondary" component="p">
              <a
                href={`mailto:${workEmail}`}
                target="_blank"
                rel="noopener noreferrer"
              >
                {workEmail}
              </a>
              <br />
              Location: {location}
              <br />
              Supervisor: {supervisorProfile?.name}
              <br />
              PDL: {pdlProfile?.name}
              <br />
            </Typography>
          </Container>
        </CardContent>
        {isAdmin && (
          <CardActions>
            <SplitButton
              className="split-button"
              options={options}
              onClick={handleAction}
            />
            <Dialog
              open={openDelete}
              onClose={handleCloseDeleteConfirmation}
              aria-labelledby="alert-dialog-title"
              aria-describedby="alert-dialog-description"
            >
              <DialogTitle id="alert-dialog-title">Delete member?</DialogTitle>
              <DialogContent>
                <DialogContentText id="alert-dialog-description">
                  Are you sure you want to delete the member's data?
                </DialogContentText>
              </DialogContent>
              <DialogActions>
                <Button onClick={handleCloseDeleteConfirmation} color="primary">
                  Cancel
                </Button>
                <Button onClick={handleDeleteMember} color="primary" autoFocus>
                  Yes
                </Button>
              </DialogActions>
            </Dialog>
            <MemberModal
              member={member}
              open={open}
              onClose={handleClose}
              onSave={async (member) => {
                let res = await updateMember(member, csrf);
                let data =
                  res.payload && res.payload.data && !res.error
                    ? res.payload.data
                    : null;
                if (data) {
                  const copy = [...memberProfiles];
                  const index = copy.findIndex(
                    (profile) => profile.id === data.id
                  );
                  copy[index] = data;
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
