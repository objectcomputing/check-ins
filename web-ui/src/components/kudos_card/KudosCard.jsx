import React, { useCallback, useEffect, useContext, useState } from 'react';
import PropTypes from 'prop-types';
import {
  Paper,
  Collapse,
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
  FormGroup,
  FormControlLabel,
  Checkbox,
  Link
} from '@mui/material';
import {
  selectCsrfToken,
  selectActiveOrInactiveProfile
} from '../../context/selectors';
import MemberSelector from '../member_selector/MemberSelector';
import { AppContext } from '../../context/AppContext';
import { getAvatarURL } from '../../api/api';
import { getCustomEmoji } from '../../api/emoji.js';
import DateFnsUtils from '@date-io/date-fns';
import CheckIcon from '@mui/icons-material/Check';
import CloseIcon from '@mui/icons-material/Close';
import EditIcon from '@mui/icons-material/Edit';
import TeamIcon from '@mui/icons-material/Groups';
import { Emoji, EmojiStyle } from 'emoji-picker-react';

import './KudosCard.css';
import emojis from 'emoji-picker-react/src/data/emojis.json';
import { approveKudos, deleteKudos, updateKudos } from '../../api/kudos';
import { UPDATE_TOAST } from '../../context/actions';

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
  }).isRequired,
  includeActions: PropTypes.bool,
  includeEdit: PropTypes.bool,
  onKudosAction: PropTypes.func
};

const KudosCard = ({ kudos, includeActions, includeEdit, onKudosAction }) => {
  const { state, dispatch } = useContext(AppContext);
  const csrf = selectCsrfToken(state);
  const [ emojiShortcodeMap, setEmojiShortcodeMap ] = useState({});
  const [ customLoaded, setCustomLoaded ] = useState(false);

  const [expanded, setExpanded] = useState(true);
  const [deleteDialogOpen, setDeleteDialogOpen] = useState(false);
  const [editDialogOpen, setEditDialogOpen] = useState(false);
  const [kudosPublic, setKudosPublic] = useState(kudos.publiclyVisible);
  const [kudosMessage, setKudosMessage] = useState(kudos.message);
  const [memberSelectorOpen, setMemberSelectorOpen] = useState(false);
  const [kudosRecipientMembers, setKudosRecipientMembers] = useState(
    kudos.recipientMembers
  );

  const sender = selectActiveOrInactiveProfile(state, kudos.senderId);

  useEffect(() => {
    let shortcodeMap = {};
    for (const category in emojis) {
      if (Object.hasOwn(emojis, category)) {
        let emojiList = emojis[category];
        emojiList.reduce((acc, current) => {
          current?.n?.forEach(
            name => (acc[name.replace(/\s/g, '_')] = { unified: current.u })
          );
          return acc;
        }, shortcodeMap);
      }
    }
    setEmojiShortcodeMap(shortcodeMap);
  }, []);

  useEffect(() => {
    const loadCustomEmoji = async () => {
      let res = await getCustomEmoji(csrf);
      if (res && res.payload && res.payload.data && !res.error) {
        const shortcodeMap = { ...emojiShortcodeMap };
        let aliases = {};
        let customEmoji = res.payload.data;
        for(const emoji in customEmoji) {
          if(Object.hasOwn(customEmoji, emoji)) {
            if(customEmoji[emoji].startsWith("alias:")) {
              aliases[emoji] = { alias: customEmoji[emoji].substring("alias:".length) };
            } else {
              shortcodeMap[emoji] = { customUrl: customEmoji[emoji] };
            }
          }
        }
        for(const emoji in aliases) {
          if (Object.hasOwn(aliases, emoji)) {
            shortcodeMap[emoji] = shortcodeMap[aliases[emoji].alias];
          }
        }
        setEmojiShortcodeMap(shortcodeMap);
        setCustomLoaded(true);
      } else {
        window.snackDispatch({
          type: UPDATE_TOAST,
          payload: {
            severity: 'warning',
            toast: `Custom emoji could not be loaded: ${res.error}`
          }
        });
      }
    }

    if(csrf && !customLoaded) {
      loadCustomEmoji();
    }
  }, [csrf, customLoaded, emojiShortcodeMap]);

  const getEmojiDataByShortcode = useCallback(shortcode => {
    return emojiShortcodeMap[shortcode.toLowerCase()] || null;
  }, [emojiShortcodeMap]);

  const regexIndexOf = useCallback((text, regex, start) => {
    const indexInSuffix = text.slice(start).search(regex);
    return indexInSuffix < 0 ? indexInSuffix : indexInSuffix + start;
  }, []);

  // Replaces occurrences of a specific name in a message string with a MUI Link component.
  const linkMember = useCallback((member, name, message) => {
    const components = [];
    let currentMessage = message;
    let currentIndex = 0;
    let lastIndex = 0;

    while (currentIndex < currentMessage.length) {
      const index = regexIndexOf(
        currentMessage,
        new RegExp('\\b' + name + '\\b', 'i'),
        currentIndex
      );
      if (index !== -1) {
        if (index > lastIndex) {
          components.push(currentMessage.slice(lastIndex, index));
        }
        components.push(
          <Link
            key={`${member.id}-${index}`}
            href={`/profile/${member.id}`}
            underline="hover"
          >
            {name}
          </Link>
        );
        currentIndex = index + name.length;
        lastIndex = currentIndex;
      } else {
        break;
      }
    }
    if (lastIndex < currentMessage.length) {
      components.push(currentMessage.slice(lastIndex));
    }
    return components.length === 0 ? [message] : components;
  }, []);

  // Generates a list of unique name variations for a member to be used for linking.
  const searchNames = useCallback((member, members) => {
    const names = [];
    if (member.middleName)
      names.push(`${member.firstName} ${member.middleName} ${member.lastName}`);
    const firstAndLast = `${member.firstName} ${member.lastName}`;
    if (
      !members.some(
        k =>
          k.id !== member.id && firstAndLast === `${k.firstName} ${k.lastName}`
      )
    )
      names.push(firstAndLast);
    if (
      !members.some(
        k =>
          k.id !== member.id &&
          (member.lastName === k.lastName || member.lastName === k.firstName)
      )
    )
      names.push(member.lastName);
    if (
      !members.some(
        k =>
          k.id !== member.id &&
          (member.firstName === k.lastName || member.firstName === k.firstName)
      )
    )
      names.push(member.firstName);
    return names;
  }, []);

  // Converts Slack-style links (<url|text> or <url>) in a string to <a> elements.
  const linkSlackUrls = useCallback(textLine => {
    const slackLinkRegex = /<([^<>|]*)(?:\|([^<>]*))?>/g;
    const components = [];
    let lastIndex = 0;
    let match;
    while ((match = slackLinkRegex.exec(textLine)) !== null) {
      const url = match[1];
      const linkText = match[2];
      if (match.index > lastIndex)
        components.push(textLine.slice(lastIndex, match.index));
      components.push(
        <a
          key={`slack-link-${match.index}`}
          href={url}
          target="_blank"
          rel="noopener noreferrer"
        >
          {linkText || url}
        </a>
      );
      lastIndex = slackLinkRegex.lastIndex;
    }
    if (lastIndex < textLine.length) components.push(textLine.slice(lastIndex));
    return components.length === 0 ? [textLine] : components;
  }, []);

  const renderTextWithEmojis = useCallback(text => {
    const emojiShortcodeRegex = /:([a-zA-Z0-9_+-]+):/g; // Regex to find :shortcodes:
    const components = [];
    let lastIndex = 0;
    let match;

    while ((match = emojiShortcodeRegex.exec(text)) !== null) {
      const shortcode = match[1];
      const emojiData = getEmojiDataByShortcode(shortcode);
      const precedingText = text.slice(lastIndex, match.index);

      // Add text before the emoji shortcode
      if (precedingText) {
        components.push(precedingText);
      }

      // Add the Emoji component or the original shortcode text
      if (emojiData) {
        if (emojiData.unified) {
          components.push(
            <Emoji
              key={`${match.index}-${shortcode}`} // Unique key
              unified={emojiData.unified}
              size={20} // Adjust size as needed
            />
          );
        } else if (emojiData.customUrl) {
          // Render custom emoji using emojiUrl
          components.push(
            <img src={emojiData.customUrl} alt={shortcode} style={{ height: '20px', width: '20px', fontSize: '20px' }} />
            // Not sure why the below doesn't work. It seems like it should according to the docs. :shrug:s
            // <Emoji
            //   key={`${match.index}-${shortcode}`}
            //   emojiUrl={emojiData.customUrl}
            //   size={20}
            // />
          );
        }
      } else {
        // If shortcode not found in map, render the original text
        components.push(match[0]);
      }

      lastIndex = emojiShortcodeRegex.lastIndex;
    }

    // Add any remaining text after the last shortcode
    const remainingText = text.slice(lastIndex);
    if (remainingText) {
      components.push(remainingText);
    }

    // If the original text had no shortcodes, return it in an array
    return components.length === 0 ? [text] : components;
  }, [getEmojiDataByShortcode]);

  // Creates the final array of React components for the message body,
  // processing Slack links, member names, and emojis.
  const createLinksAndEmojis = useCallback(
    kudosData => {
      const lines = [];
      let lineIndex = 0;
      const recipients = Array.isArray(kudosData.recipientMembers)
        ? kudosData.recipientMembers
        : [];

      for (const line of kudosData.message.split('\n')) {
        let components = linkSlackUrls(line);

        // Process Member Name Links
        let componentsAfterNames = [];
        for (const component of components) {
          if (typeof component === 'string') {
            let currentStringSegments = [component];
            for (const member of recipients) {
              const names = searchNames(member, recipients);
              let nextStringSegments = [];
              for (const segment of currentStringSegments) {
                if (typeof segment === 'string') {
                  let segmentProcessed = false;
                  for (const name of names) {
                    const built = linkMember(member, name, segment);
                    if (
                      built.length > 1 ||
                      (built.length === 1 && built[0] !== segment)
                    ) {
                      nextStringSegments.push(...built);
                      segmentProcessed = true;
                      break;
                    }
                  }
                  if (!segmentProcessed) nextStringSegments.push(segment);
                } else {
                  nextStringSegments.push(segment);
                }
              }
              currentStringSegments = nextStringSegments;
            }
            componentsAfterNames.push(...currentStringSegments);
          } else {
            componentsAfterNames.push(component);
          }
        }
        components = componentsAfterNames;

        let finalComponents = [];
        for (const comp of components) {
          if (typeof comp === 'string') {
            finalComponents.push(...renderTextWithEmojis(comp)); // Spread the result
          } else {
            finalComponents.push(comp); // Keep existing non-string components
          }
        }

        lines.push(
          <Typography
            key={`${kudosData.id}-line-${lineIndex}`}
            variant="body1"
            component="div"
            sx={{ lineHeight: '1.6' /* Improve spacing with emojis */ }}
          >
            {finalComponents}
          </Typography>
        );
        lineIndex++;
      }
      return lines;
    },
    [
      kudos.id,
      kudos.message,
      kudos.recipientMembers,
      linkMember,
      linkSlackUrls,
      searchNames,
      renderTextWithEmojis
    ]
  );

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
  },[]);

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

  const approveKudosCallback = useCallback(
    async event => {
      event.stopPropagation();
      const res = await approveKudos(kudos, csrf);
      if (res?.payload?.data && !res.error) {
        dispatch({
          type: UPDATE_TOAST,
          payload: {
            severity: 'success',
            toast: 'Kudos approved'
          }
        });
        onKudosAction && onKudosAction();
      } else {
        dispatch({
          type: UPDATE_TOAST,
          payload: {
            severity: 'error',
            toast: 'Failed to approve kudos'
          }
        });
      }
    },
    [kudos, csrf, dispatch, onKudosAction]
  );

  const deleteKudosCallback = useCallback(async () => {
    setDeleteDialogOpen(false);
    const res = await deleteKudos(kudos.id, csrf);
    if (res?.payload?.status === 204 && !res.error) {
      dispatch({
        type: UPDATE_TOAST,
        payload: {
          severity: 'success',
          toast: 'Kudos deleted'
        }
      });
      onKudosAction && onKudosAction();
    } else {
      dispatch({
        type: UPDATE_TOAST,
        payload: {
          severity: 'error',
          toast: 'Failed to delete kudos'
        }
      });
    }
  }, [kudos, csrf, dispatch, onKudosAction]);

  const updateKudosCallback = useCallback(async () => {
    // Close the dialog.
    setEditDialogOpen(false);

    // Update the modifiable parts.
    const proposed = {
      id: kudos.id,
      message: kudosMessage,
      publiclyVisible: kudosPublic,
      recipientMembers: kudosRecipientMembers
    };

    // Update on the server.
    const res = await updateKudos(proposed, csrf);
    if (res.error) {
      dispatch({
        type: UPDATE_TOAST,
        payload: {
          severity: 'error',
          toast: 'Failed to update kudos'
        }
      });
    } else {
      dispatch({
        type: UPDATE_TOAST,
        payload: {
          severity: 'success',
          toast: 'Kudos Updated'
        }
      });
      onKudosAction && onKudosAction();
    }
  }, [
    kudos,
    kudosMessage,
    kudosPublic,
    kudosRecipientMembers,
    csrf,
    dispatch,
    onKudosAction
  ]);

  const getStatusComponent = useCallback(() => {
    const dateApproved = kudos.dateApproved
      ? new Date(kudos.dateApproved.join('/'))
      : null;

    const info = [];
    const actions = [];
    if (includeActions) {
      actions.push(
        <Tooltip key="approve" arrow title="Approve">
          <Button
            variant="outlined"
            color="success"
            size="small"
            onClick={approveKudosCallback}
          >
            <CheckIcon />
          </Button>
        </Tooltip>
      );
    } else if (dateApproved) {
      info.push(
        <Typography key="received" color="green" variant="body2">
          Received{' '}
          {dateApproved ? dateUtils.format(dateApproved, 'MM/dd/yyyy') : ''}
        </Typography>
      );
    } else {
      const dateCreated = new Date(kudos.dateCreated.join('/'));
      if (kudos.publiclyVisible) {
        info.push(
          <Typography key="pending" color="orange" variant="body2">
            Pending
          </Typography>
        );
      }
      info.push(
        <Typography key="created" variant="body2" color="gray" fontSize="10px">
          Created {dateUtils.format(dateCreated, 'MM/dd/yyyy')}
        </Typography>
      );
    }
    if (includeEdit) {
      actions.push(
        <Tooltip key="edit" arrow title="Edit">
          <Button
            variant="outlined"
            size="small"
            onClick={event => {
              event.stopPropagation();
              reloadKudosValues();
              setEditDialogOpen(true);
            }}
          >
            <EditIcon />
          </Button>
        </Tooltip>
      );
    }
    if (includeActions || includeEdit) {
      actions.push(
        <Tooltip key="delete" arrow title="Delete">
          <Button
            variant="outlined"
            color="error"
            size="small"
            onClick={event => {
              event.stopPropagation();
              setDeleteDialogOpen(true);
            }}
          >
            <CloseIcon />
          </Button>
        </Tooltip>
      );
    }
    return (
      <>
        {info.length > 0 && <div>{info}</div>}
        {actions.length > 0 && (
          <div className="kudos-action-buttons">{actions}</div>
        )}
      </>
    );
  }, [kudos, includeActions, includeEdit, approveKudosCallback]);

  const reloadKudosValues = () => {
    setKudosMessage(kudos.message);
    setKudosPublic(kudos.publiclyVisible);
    setKudosRecipientMembers(kudos.recipientMembers);
  };

  const dateApproved = kudos.dateApproved
    ? new Date(kudos.dateApproved.join('/'))
    : null;

  return (
    <>
      <Dialog open={deleteDialogOpen}>
        <DialogTitle>Delete Kudos</DialogTitle>
        <DialogContent>
          <DialogContentText>
            Are you sure you want to complete this action? The kudos will be
            deleted.
          </DialogContentText>
        </DialogContent>
        <DialogActions>
          <Button
            onClick={() => setDeleteDialogOpen(false)}
            style={{ color: 'gray' }}
          >
            Cancel
          </Button>
          <Button onClick={deleteKudosCallback} color="error" autoFocus>
            Delete
          </Button>
        </DialogActions>
      </Dialog>
      <Dialog open={editDialogOpen} maxWidth="sm" fullWidth>
        <DialogTitle>Edit Kudos</DialogTitle>
        <DialogContent>
          <MemberSelector
            onChange={setKudosRecipientMembers}
            selected={kudosRecipientMembers}
          />
          <FormGroup>
            <FormControlLabel
              control={<Checkbox checked={kudosPublic} />}
              label="Public"
              onChange={e => {
                setKudosPublic(e.target.checked);
              }}
            />
          </FormGroup>
          <TextField
            id="kudos-text"
            fullWidth
            multiline
            defaultValue={kudosMessage}
            onChange={event => {
              setKudosMessage(event.target.value);
            }}
            rows={5}
            style={{ marginTop: '2rem' }}
            variant="outlined"
          />
        </DialogContent>
        <DialogActions>
          <Button onClick={() => setEditDialogOpen(false)}>Cancel</Button>
          <Button
            onClick={updateKudosCallback}
            disabled={
              kudosMessage.trim().length == 0 ||
              kudosRecipientMembers.length == 0
            }
            autoFocus
          >
            Save
          </Button>
        </DialogActions>
      </Dialog>
      <Paper className="kudos-card">
        <div
          className="kudos-card-header"
          onClick={() => setExpanded(!expanded)}
        >
          <div className="members-container">
            {getRecipientComponent()}
            <Typography variant="body1">received kudos from</Typography>
            <Chip
              avatar={<Avatar src={getAvatarURL(sender?.workEmail)} />}
              label={sender?.name}
            />
          </div>
          <div className="kudos-status-container">{getStatusComponent()}</div>
        </div>
        <Divider />
        <Collapse in={expanded}>
          <div className="kudos-card-content">
            <>{createLinksAndEmojis(kudos)}</>
            {kudos.recipientMembers?.length > 1 && (
              <div className="kudos-recipient-list">
                <Typography variant="body2">
                  {kudos.recipientTeam ? 'Team Members:' : 'Members:'}
                </Typography>
                {kudos.recipientMembers.map(recipient => (
                  <Chip
                    key={recipient.id}
                    avatar={<Avatar src={getAvatarURL(recipient?.workEmail)} />}
                    label={`${recipient.firstName} ${recipient.lastName}`}
                  />
                ))}
              </div>
            )}
          </div>
        </Collapse>
      </Paper>
    </>
  );
};

KudosCard.propTypes = propTypes;

export default KudosCard;
