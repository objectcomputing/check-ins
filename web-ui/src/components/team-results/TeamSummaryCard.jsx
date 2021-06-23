import React, { useContext, useState } from "react";

import { AppContext } from "../../context/AppContext";
import { UPDATE_TEAMS, UPDATE_TOAST } from "../../context/actions";
import EditTeamModal from "./EditTeamModal";

import {
  Button,
  Card,
  CardActions,
  CardContent,
  CardHeader,
  Dialog,
  DialogActions,
  DialogContent,
  DialogContentText,
  DialogTitle,
} from "@material-ui/core";
import PropTypes from "prop-types";
import { deleteTeam, updateTeam } from "../../api/team.js";
import SplitButton from "../split-button/SplitButton";

import { makeStyles } from "@material-ui/core/styles";
const useStyles = makeStyles((theme) => ({
  card: {
    width: "340px",
    display: "flex",
    flexDirection: "column",
    justifyContent: "space-between",
  },
  header: {
    width: "100%",
  },
  title: {
    overflow: "hidden",
    textOverflow: "ellipsis",
    whiteSpace: "nowrap",
  },
}));

const propTypes = {
  team: PropTypes.shape({
    id: PropTypes.string,
    name: PropTypes.string,
    description: PropTypes.string,
  }),
};

const displayName = "TeamSummaryCard";

const TeamSummaryCard = ({ team, index }) => {
  const classes = useStyles();
  const { state, dispatch } = useContext(AppContext);
  const { teams, userProfile, csrf } = state;
  const { id } = team;
  const [open, setOpen] = useState(false);
  const [openDelete, setOpenDelete] = useState(false);
  const isAdmin =
    userProfile && userProfile.role && userProfile.role.includes("ADMIN");

  let leads =
    team.teamMembers == null
      ? null
      : team.teamMembers.filter((teamMember) => teamMember.lead);
  let nonLeads =
    team.teamMembers == null
      ? null
      : team.teamMembers.filter((teamMember) => !teamMember.lead);

  const isTeamLead =
    leads === null
      ? false
      : leads.some((lead) => lead.memberId === userProfile.memberProfile.id);

  const handleOpen = () => setOpen(true);
  const handleOpenDeleteConfirmation = () => setOpenDelete(true);

  const handleClose = () => setOpen(false);
  const handleCloseDeleteConfirmation = () => setOpenDelete(false);

  const deleteATeam = async () => {
    if (id && csrf) {
      const result = await deleteTeam(id, csrf);
      if (result && result.payload && result.payload.status === 200) {
        window.snackDispatch({
          type: UPDATE_TOAST,
          payload: {
            severity: "success",
            toast: "Team deleted",
          },
        });
        let newTeams = teams.filter((team) => {
          return team.id !== id;
        });
        dispatch({
          type: UPDATE_TEAMS,
          payload: newTeams,
        });
      }
    }
  };

  const options =
    isAdmin || isTeamLead ? ["Edit Team", "Delete Team"] : ["Edit Team"];

  const handleAction = (e, index) => {
    if (index === 0) {
      handleOpen();
    } else if (index === 1) {
      handleOpenDeleteConfirmation();
    }
  };

  return (
    <Card className={classes.card}>
      <CardHeader
        classes={{
          content: classes.header,
          title: classes.title,
          subheader: classes.title,
        }}
        title={team.name}
        subheader={team.description}
      />
      <CardContent>
        {team.teamMembers == null ? (
          <React.Fragment>
            <strong>Team Leads: </strong>None Assigned
            <br />
            <strong>Team Members: </strong>None Assigned
          </React.Fragment>
        ) : (
          <React.Fragment>
            <strong>Team Leads: </strong>
            {leads.map((lead, index) => {
              return index !== leads.length - 1 ? `${lead.name}, ` : lead.name;
            })}
            <br />
            <strong>Team Members: </strong>
            {nonLeads.map((member, index) => {
              return index !== nonLeads.length - 1
                ? `${member.name}, `
                : member.name;
            })}
          </React.Fragment>
        )}
      </CardContent>
      <CardActions>
        {(isAdmin || isTeamLead) && (
          <div>
            <SplitButton options={options} onClick={handleAction} />
            <Dialog
              open={openDelete}
              onClose={handleCloseDeleteConfirmation}
              aria-labelledby="alert-dialog-title"
              aria-describedby="alert-dialog-description"
            >
              <DialogTitle id="alert-dialog-title">Delete team?</DialogTitle>
              <DialogContent>
                <DialogContentText id="alert-dialog-description">
                  Are you sure you want to delete the team?
                </DialogContentText>
              </DialogContent>
              <DialogActions>
                <Button onClick={handleCloseDeleteConfirmation} color="primary">
                  Cancel
                </Button>
                <Button onClick={deleteATeam} color="primary" autoFocus>
                  Yes
                </Button>
              </DialogActions>
            </Dialog>
          </div>
        )}
      </CardActions>
      <EditTeamModal
        team={team}
        open={open}
        onClose={handleClose}
        onSave={async (editedTeam) => {
          const res = await updateTeam(editedTeam, csrf);
          const data =
            res.payload && res.payload.data && !res.error
              ? res.payload.data
              : null;
          if (data) {
            if (data.teamMembers) {
              for (let savedMember of data.teamMembers) {
                let memberIndex = editedTeam.teamMembers.findIndex((member) => {
                  return savedMember.memberId === member.memberId;
                });
                editedTeam.teamMembers[memberIndex] = savedMember;
              }
            }

            const copy = [...teams];
            copy[index] = editedTeam;

            dispatch({
              type: UPDATE_TEAMS,
              payload: copy,
            });
            handleClose();
          }
        }}
        headerText="Edit Your Team"
      />
    </Card>
  );
};

TeamSummaryCard.displayName = displayName;
TeamSummaryCard.propTypes = propTypes;

export default TeamSummaryCard;
