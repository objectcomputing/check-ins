import React, { useCallback, useContext, useState } from 'react';
import PropTypes from 'prop-types';
import {
  Card,
  CardHeader,
  CardContent,
  Divider,
  Typography,
  Avatar,
  Chip,
  Button,
  AvatarGroup,
  Tooltip,
  Dialog,
  DialogTitle,
  DialogContent,
  DialogContentText,
  DialogActions,
  TextField,
  Link
} from '@mui/material';
import {
  selectCsrfToken,
  selectActiveOrInactiveProfile
} from '../../context/selectors';
import { AppContext } from '../../context/AppContext';
import { getAvatarURL } from '../../api/api';
import DateFnsUtils from '@date-io/date-fns';
import CheckIcon from '@mui/icons-material/Check';
import CloseIcon from '@mui/icons-material/Close';
import TeamIcon from '@mui/icons-material/Groups';

import { approveKudos, deleteKudos } from '../../api/kudos';
import { UPDATE_TOAST } from '../../context/actions';

import './PublicKudosCard.css';

const dateUtils = new DateFnsUtils();

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

  const regexIndexOf = (text, regex, start) => {
    const indexInSuffix = text.slice(start).search(regex);
    return indexInSuffix < 0 ? indexInSuffix : indexInSuffix + start;
  };

  const linkMember = (member, name, message) => {
    const components = [];
    let index = 0;
    do {
      index = regexIndexOf(
        message,
        new RegExp('\\b' + name + '\\b', 'i'),
        index
      );
      if (index != -1) {
        const link = (
          <Link key={`${member.id}-${index}`} href={`/profile/${member.id}`}>
            {name}
          </Link>
        );
        if (index > 0) {
          components.push(message.slice(0, index));
        }
        components.push(link);
        message = message.slice(index + name.length);
      }
    } while (index != -1);
    components.push(message);
    return components;
  };

  const searchNames = (member, members) => {
    const names = [];
    if (member.middleName) {
      names.push(`${member.firstName} ${member.middleName} ${member.lastName}`);
    }
    const firstAndLast = `${member.firstName} ${member.lastName}`;
    if (
      !members.some(
        k => k.id != member.id && firstAndLast == `${k.firstName} ${k.lastName}`
      )
    ) {
      names.push(firstAndLast);
    }
    if (
      !members.some(
        k =>
          k.id != member.id &&
          (member.lastName == k.lastName || member.lastName == k.firstName)
      )
    ) {
      // If there are no other recipients with a name that contains this
      // member's last name, we can replace based on that.
      names.push(member.lastName);
    }
    if (
      !members.some(
        k =>
          k.id != member.id &&
          (member.firstName == k.lastName || member.firstName == k.firstName)
      )
    ) {
      // If there are no other recipients with a name that contains this
      // member's first name, we can replace based on that.
      names.push(member.firstName);
    }
    return names;
  };

  const linkSlackUrls = textLine => {
    // Regex to find <url> or <url|text>
    // Group 1: URL
    // Group 2: Optional Link Text (undefined if not present)
    const slackLinkRegex = /<([^<>|]*)(?:\|([^<>]*))?>/g;
    const components = [];
    let lastIndex = 0;
    let match;

    // Find all matches in the text line
    while ((match = slackLinkRegex.exec(textLine)) !== null) {
      const url = match[1];
      const linkText = match[2]; // Will be undefined if there's no |text part
      const precedingText = textLine.slice(lastIndex, match.index);

      // Add the text before the match (if any)
      if (precedingText) {
        components.push(precedingText);
      }

      // Create and add the link component
      components.push(
        <a
          key={`slack-link-${match.index}`} // Unique key based on position
          href={url}
          target="_blank" // Open in new tab
          rel="noopener noreferrer" // Security measure
        >
          {linkText || url}
        </a>
      );

      // Update the index for the next slice
      lastIndex = slackLinkRegex.lastIndex;
    }

    // Add any remaining text after the last match
    const remainingText = textLine.slice(lastIndex);
    if (remainingText) {
      components.push(remainingText);
    }

    // If no links were found at all, return the original line in an array
    if (components.length === 0) {
      return [textLine];
    }

    return components;
  };

  const createLinks = kudos => {
    const lines = [];
    let index = 0;
    for (let line of kudos.message.split('\n')) {
      const components = linkSlackUrls(line);
      for (let member of kudos.recipientMembers) {
        const names = searchNames(member, kudos.recipientMembers);
        for (let name of names) {
          for (let i = 0; i < components.length; i++) {
            const component = components[i];
            if (typeof component === 'string') {
              const built = linkMember(member, name, component);
              if (built.length > 1) {
                components.splice(i, 1, ...built);
              }
            }
          }
        }
      }
      lines.push(
        <Typography key={kudos.id + '-' + index} variant="body1">
          {components}
        </Typography>
      );
      index++;
    }
    return lines;
  };

  const multiTooltip = (num, list) => {
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
  };

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
        <>{createLinks(kudos)}</>
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
