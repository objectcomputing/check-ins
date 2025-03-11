import React, {useContext, useState} from 'react';
import {styled} from '@mui/material/styles';
import {AppContext} from '../../context/AppContext';
import {UPDATE_TEAMS} from '../../context/actions';
import EditTeamModal from './EditTeamModal';
import KudosDialog from '../kudos_dialog/KudosDialog';
import {Link} from 'react-router-dom';
import {Card, CardActions, CardContent, CardHeader, Tooltip, Typography,} from '@mui/material';
import PropTypes from 'prop-types';
import {updateTeam} from '../../api/team.js';
import SplitButton from '../split-button/SplitButton';
import {selectCurrentUser, selectIsAdmin} from "../../context/selectors.js";

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
    justifyContent: 'space-between',
    position: 'relative',
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

const inactiveStyle = {
  'color': 'var(--action-disabled)',
  'font-size': '0.75em',
};

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
  const { teams, csrf } = state;
  const [openKudos, setOpenKudos] = useState(false);
  // const [selectedTeam, setSelectedTeam] = useState(null);
  const [tooltipIsOpen, setTooltipIsOpen] = useState(false);

  const isAdmin = selectIsAdmin(state);
  const currentUser = selectCurrentUser(state);

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
      : leads.some(lead => lead.memberId === currentUser.id);

  const handleOpenKudos = () => setOpenKudos(true);
  const handleCloseKudos = () => setOpenKudos(false);

  const options = ['Edit Team', 'Give Kudos'];

  const handleAction = (e, index) => {
    if (index === 0) {
      onTeamSelect(team.id);
    } else if (index === 1) {
      handleOpenKudos();
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
        {!team.active && (
          <Typography sx={{ position: 'absolute', top: 10, right: 10,
                            ...inactiveStyle,
                          }}
          >
            Inactive
          </Typography>
        )}
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
            <KudosDialog
              open={openKudos}
              onClose={handleCloseKudos}
              teamId={null}
            />
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
