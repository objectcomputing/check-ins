import React, { useContext, useState, useCallback } from 'react';
import { Link } from 'react-router-dom';

import { Construction, Groups } from '@mui/icons-material';
import { styled } from '@mui/material/styles';

import { AppContext } from '../../context/AppContext';
import { UPDATE_GUILDS, UPDATE_TOAST } from '../../context/actions';
import EditGuildModal from './EditGuildModal';

import {
  Button,
  Card,
  CardActions,
  CardContent,
  CardHeader,
  Dialog,
  DialogContentText,
  DialogTitle,
  DialogContent,
  DialogActions,
  Link as StyledLink,
  Tooltip,
  Typography,
} from '@mui/material';
import PropTypes from 'prop-types';
import { updateGuild } from '../../api/guild.js';

const PREFIX = 'GuildSummaryCard';
const classes = {
  card: `${PREFIX}-card`,
  header: `${PREFIX}-header`,
  title: `${PREFIX}-title`
};
const StyledCard = styled(Card)(() => ({
  [`&.${classes.card}`]: {
    display: 'flex',
    flexDirection: 'column',
    justifyContent: 'space-between',
    position: 'relative',
    width: '340px'
  },
  [`& .${classes.header}`]: {
    width: '100%'
  },
  ['& [data-icon]']: {
    position: 'absolute',
    right: '1rem',
    top: '1rem'
  },
  [`& .${classes.title}`]: {
    overflow: 'hidden',
    textOverflow: 'ellipsis',
    whiteSpace: 'nowrap'
  }
}));

const inactiveStyle = {
  'color': 'var(--action-disabled)',
  'font-size': '0.75em',
};

const propTypes = {
  guild: PropTypes.shape({
    id: PropTypes.string,
    name: PropTypes.string,
    description: PropTypes.string
  })
};

const displayName = 'GuildSummaryCard';

const GuildSummaryCard = ({ guild, index, isOpen, onGuildSelect }) => {
  const { state, dispatch } = useContext(AppContext);
  const { guilds, userProfile, csrf } = state;
  const [open, setOpen] = useState(isOpen);
  const [tooltipIsOpen, setTooltipIsOpen] = useState(false);
  const isAdmin =
    userProfile && userProfile.role && userProfile.role.includes('ADMIN');

  let leads =
    guild.guildMembers == null
      ? null
      : guild.guildMembers.filter(guildMember => guildMember.lead);
  let nonLeads =
    guild.guildMembers == null
      ? null
      : guild.guildMembers.filter(guildMember => !guildMember.lead);

  const isGuildLead =
    leads === null
      ? false
      : leads.some(lead => lead.memberId === userProfile.memberProfile.id);

  const handleOpen = () => {
    setOpen(true);
    onGuildSelect(guild.id);
  };
  const handleClose = () => {
    setOpen(false);
    onGuildSelect('');
  };

  const iconStyles = {
    position: 'absolute',
    bottom: '0.5rem',
    right: '0.5rem',
    height: '2.5rem',
    width: '2.5rem'
  };

  const guildIcon = () => (
    <Tooltip
      title={`This is a ${guild.community ? 'Community' : 'Guild'}.`}
      aria-label="icon meaning"
    >
      {guild.community ? (
        <Groups sx={{ color: 'var(--oci-orange)', ...iconStyles }} />
      ) : (
        <Construction sx={{ color: 'var(--oci-light-blue)', ...iconStyles }} />
      )}
    </Tooltip>
  );

  return (
    <StyledCard className={classes.card} sx={{ position: 'relative' }}>
      <CardHeader
        classes={{
          content: classes.header,
          title: classes.title,
          subheader: classes.title
        }}
        title={guild.name}
        subheader={
          <Tooltip
            open={tooltipIsOpen}
            onOpen={() => setTooltipIsOpen(true)}
            onClose={() => setTooltipIsOpen(false)}
            enterTouchDelay={0}
            placement="top-start"
            title={guild.description}
          >
            <div>{guild.description}</div>
          </Tooltip>
        }
      />
      <CardContent>
        {!guild.active && (
          <Typography sx={{ position: 'absolute', top: 10, right: 10,
                            ...inactiveStyle,
                          }}
          >
            Inactive
          </Typography>
        )}
        {guild?.link ? (
          <React.Fragment>
            <div>
              <StyledLink href={guild.link}>Link to Guild Homepage</StyledLink>
            </div>
          </React.Fragment>
        ) : null}
        {guild.guildMembers == null ? (
          <React.Fragment>
            <strong>Guild Leads: </strong>None Assigned
            <br />
            <strong>Guild Members: </strong>None Assigned
          </React.Fragment>
        ) : (
          <React.Fragment>
            <strong>Guild Leads: </strong>
            {leads.map((lead, index) => {
              return (
                <Link
                  key={lead.memberId}
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
            <strong>Guild Members: </strong>
            {nonLeads.map((member, index) => {
              return (
                <Link
                  key={member.memberId}
                  to={`/profile/${member?.memberId}`}
                  style={{
                    textDecoration: 'none',
                    color: 'inherit'
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
        {(isAdmin || isGuildLead) && (
          <>
            <Button onClick={handleOpen}>Edit Guild</Button>
          </>
        )}
      </CardActions>
      {guildIcon()}
      <EditGuildModal
        guild={guild}
        open={open}
        onClose={handleClose}
        onSave={async editedGuild => {
          const res = await updateGuild(editedGuild, csrf);
          const data =
            res.payload?.data && !res.error ? res.payload.data : null;
          if (data) {
            const copy = [...guilds];
            copy[index] = data;
            dispatch({
              type: UPDATE_GUILDS,
              payload: copy
            });
          }
        }}
        headerText="Edit Your Guild"
      />
    </StyledCard>
  );
};

GuildSummaryCard.displayName = displayName;
GuildSummaryCard.propTypes = propTypes;

export default GuildSummaryCard;
