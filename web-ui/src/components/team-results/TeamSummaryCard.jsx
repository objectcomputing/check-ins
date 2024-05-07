import React, { useContext, useState, useCallback } from 'react';
import { styled } from '@mui/material/styles';
import { AppContext } from '../../context/AppContext';
import { UPDATE_TEAMS, UPDATE_TOAST } from '../../context/actions';
import EditTeamModal from './EditTeamModal';
import { Link } from 'react-router-dom';
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
  Tooltip
} from '@mui/material';
import PropTypes from 'prop-types';
import { deleteTeam, updateTeam } from '../../api/team.js';
import SplitButton from '../split-button/SplitButton';

const PREFIX = 'TeamSummaryCard';
const classes = {
  card: `${PREFIX}-card`,
  header: `${PREFIX}-header`,
  title: `${PREFIX}-title`
};

const StyledCard = styled(Card)({
  [`&.${classes.card}`]: {
    width: '340px',
    display: 'flex',
    flexDirection: 'column',
    justifyContent: 'space-between'
  },
  [`& .${classes.header}`]: {
    width: '100%'
  },
  [`& .${classes.title}`]: {
    overflow: 'hidden',
    textOverflow: 'ellipsis',
    whiteSpace: 'nowrap'
  }
});

const propTypes = {
  team: PropTypes.shape({
    id: PropTypes.string,
    name: PropTypes.string,
    description: PropTypes.string
  })
};

const displayName = 'TeamSummaryCard';

const TeamSummaryCard = ({ team, index, onTeamSelect, selectedTeamId }) => {
  const { state, dispatch } = useContext(AppContext);
  const { teams, userProfile, csrf } = state;
  const [openDelete, setOpenDelete] = useState(false);
  const [tooltipIsOpen, setTooltipIsOpen] = useState(false);

  const isAdmin =
    userProfile && userProfile.role && userProfile.role.includes('ADMIN');

  let leads =
    team.teamMembers == null
      ? null
      : team.teamMembers.filter(teamMember => teamMember.lead);
  let nonLeads =
    team.teamMembers == null
      ? null
      : team.teamMembers.filter(teamMember => !teamMember.lead);

  const isTeamLead =
    leads === null
      ? false
      : leads.some(lead => lead.memberId === userProfile.memberProfile.id);

  const handleOpenDeleteConfirmation = () => setOpenDelete(true);

  const handleCloseDeleteConfirmation = () => setOpenDelete(false);

  const teamId = team?.id;
  const deleteATeam = useCallback(async () => {
    if (teamId && csrf) {
      const result = await deleteTeam(teamId, csrf);
      if (result && result.payload && result.payload.status === 200) {
        window.snackDispatch({
          type: UPDATE_TOAST,
          payload: {
            severity: 'success',
            toast: 'Team deleted'
          }
        });
        let newTeams = teams.filter(team => {
          return team.id !== teamId;
        });
        dispatch({
          type: UPDATE_TEAMS,
          payload: newTeams
        });
      }
    }
  }, [teamId, csrf, dispatch, teams]);

  const options =
    isAdmin || isTeamLead ? ['Edit Team', 'Delete Team'] : ['Edit Team'];

  const handleAction = (e, index) => {
    if (index === 0) {
      onTeamSelect(team.id);
    } else if (index === 1) {
      handleOpenDeleteConfirmation();
    }
  };

  return (
    <StyledCard className={classes.card}>
      <CardHeader
        classes={{
          content: classes.header,
          title: classes.title,
          subheader: classes.title
        }}
        title={team.name}
        subheader={
          <Tooltip
            open={tooltipIsOpen}
            onOpen={() => setTooltipIsOpen(true)}
            onClose={() => setTooltipIsOpen(false)}
            enterTouchDelay={0}
            placement="top-start"
            title={team.description}
          >
            <div>{team.description}</div>
          </Tooltip>
        }
      />
      <CardContent>
        {team.teamMembers == null ? (
          <React.Fragment key={`empty-team-${team.name}`}>
            <strong>Team Leads: </strong>None Assigned
            <br />
            <strong>Team Members: </strong>None Assigned
          </React.Fragment>
        ) : (
          <React.Fragment key={`active-team-${team.name}`}>
            <strong>Team Leads: </strong>
            {leads.map((lead, index) => {
              return (
                <Link
                  key={lead?.memberId}
                  to={`/profile/${lead?.memberId}`}
                  style={{
                    textDecoration: 'none',
                    color: 'inherit'
                  }}
                >
                  {index !== leads.length - 1 ? `${lead?.name}, ` : lead?.name}
                </Link>
              );
            })}
            <br />
            <strong>Team Members: </strong>
            {nonLeads.map((member, index) => {
              return (
                <Link
                  key={member?.memberId}
                  to={`/profile/${member?.memberId}`}
                  style={{
                    color: 'inherit',
                    opacity: 0.87,
                    textDecoration: 'none'
                  }}
                >
                  {index !== nonLeads.length - 1
                    ? `${member?.name}, `
                    : member?.name}
                </Link>
              );
            })}
          </React.Fragment>
        )}
      </CardContent>
      <CardActions>
        {(isAdmin || isTeamLead) && (
          <>
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
          </>
        )}
      </CardActions>
      <EditTeamModal
        team={team}
        open={team.id === selectedTeamId}
        onClose={() => onTeamSelect('')}
        onSave={async editedTeam => {
          const res = await updateTeam(editedTeam, csrf);
          const data =
            res.payload && res.payload.data && !res.error
              ? res.payload.data
              : null;
          if (data) {
            const copy = [...teams];
            copy[index] = data;
            dispatch({
              type: UPDATE_TEAMS,
              payload: copy
            });
          }
        }}
        headerText="Edit Your Team"
      />
    </StyledCard>
  );
};

TeamSummaryCard.displayName = displayName;
TeamSummaryCard.propTypes = propTypes;

export default TeamSummaryCard;
