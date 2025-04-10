import React, { useCallback, useEffect, useState, useContext } from 'react';
import PropTypes from 'prop-types';
import {
  Card,
  CardHeader,
  CardContent,
  Typography,
  Avatar,
  Chip,
  AvatarGroup,
  Tooltip
} from '@mui/material';
import {
  selectCsrfToken,
  selectActiveOrInactiveProfile
} from '../../context/selectors';
import { AppContext } from '../../context/AppContext';
import { getAvatarURL } from '../../api/api';
import EnhancedKudos from './EnhancedKudos';
import TeamIcon from '@mui/icons-material/Groups';

import './PublicKudosCard.css';

const propTypes = {
  kudos: PropTypes.shape({
    id: PropTypes.string.isRequired,
    message: PropTypes.string.isRequired,
    senderId: PropTypes.string.isRequired,
    recipientTeam: PropTypes.object,
    dateCreated: PropTypes.array.isRequired,
    dateApproved: PropTypes.array,
    recipientMembers: PropTypes.array
  }).isRequired
};

const KudosCard = ({ kudos }) => {
  const { state, dispatch } = useContext(AppContext);
  const csrf = selectCsrfToken(state);
  const sender = selectActiveOrInactiveProfile(state, kudos.senderId);

  const multiTooltip = useCallback((num, list) => {
    let tooltip = '';
    let prefix = '';
    for (let member of list.slice(-num)) {
      tooltip += prefix + `${member.firstName} ${member.lastName}`;
      prefix = ', ';
    }
    return (
      <Tooltip arrow title={tooltip}>
        <Typography>{`+${num}`}</Typography>
      </Tooltip>
    );
  }, []);

  const getRecipientComponent = useCallback(() => {
    if (kudos.recipientTeam) {
      return (
        <Tooltip
          arrow
          key={kudos.recipientTeam.id}
          title={kudos.recipientTeam.name}
        >
          <Avatar>
            <TeamIcon />
          </Avatar>
        </Tooltip>
      );
    }

    return (
      <AvatarGroup
        max={4}
        renderSurplus={extra => multiTooltip(extra, kudos.recipientMembers)}
      >
        {kudos.recipientMembers.map(member => (
          <Tooltip
            arrow
            key={member.id}
            title={`${member.firstName} ${member.lastName}`}
          >
            <Avatar src={getAvatarURL(member.workEmail)} />
          </Tooltip>
        ))}
      </AvatarGroup>
    );
  }, [kudos]);

  let titleText = kudos?.recipientTeam?.name
    ? 'Kudos, ' + kudos?.recipientTeam?.name + '!'
    : 'Kudos!';
  if (
    kudos?.recipientMembers?.length === 1 &&
    kudos?.recipientMembers[0]?.firstName
  )
    titleText = 'Kudos, ' + kudos?.recipientMembers[0]?.firstName + '!';

  return (
    <Card className="kudos-card">
      <CardHeader
        avatar={getRecipientComponent()}
        title={titleText}
        titleTypographyProps={{ variant: 'h5' }}
        subheader={
          <>
            from{' '}
            <Chip
              size="small"
              avatar={<Avatar src={getAvatarURL(sender?.workEmail)} />}
              label={sender?.name}
            />
          </>
        }
        subheaderTypographyProps={{ variant: 'subtitle1' }}
      />
      <CardContent>
        <EnhancedKudos kudos={kudos} />
        {kudos.recipientTeam && (
          <AvatarGroup
            max={12}
            renderSurplus={extra => multiTooltip(extra, kudos.recipientMembers)}
          >
            {kudos.recipientMembers.map(member => (
              <Tooltip
                arrow
                key={member.id}
                title={`${member.firstName} ${member.lastName}`}
              >
                <Avatar src={getAvatarURL(member.workEmail)} />
              </Tooltip>
            ))}
          </AvatarGroup>
        )}
      </CardContent>
    </Card>
  );
};

KudosCard.propTypes = propTypes;

export default KudosCard;
