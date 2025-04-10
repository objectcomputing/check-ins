import React, { useCallback, useContext, useEffect, useState } from 'react';
import PropTypes from 'prop-types';
import { Typography, Link } from '@mui/material';
import { AppContext } from '../../context/AppContext';
import { selectCsrfToken } from '../../context/selectors';
import { UPDATE_TOAST } from '../../context/actions';
import { getCustomEmoji } from '../../api/emoji.js';
import { Emoji } from 'emoji-picker-react';

import emojis from 'emoji-picker-react/src/data/emojis.json';

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

const EnhancedKudos = ({ kudos }) => {
  const { state, dispatch } = useContext(AppContext);
  const csrf = selectCsrfToken(state);
  const [emojiShortcodeMap, setEmojiShortcodeMap] = useState({});
  const [customLoaded, setCustomLoaded] = useState(false);

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
        for (const emoji in customEmoji) {
          if (Object.hasOwn(customEmoji, emoji)) {
            if (customEmoji[emoji].startsWith('alias:')) {
              aliases[emoji] = {
                alias: customEmoji[emoji].substring('alias:'.length)
              };
            } else {
              shortcodeMap[emoji] = { customUrl: customEmoji[emoji] };
            }
          }
        }
        for (const emoji in aliases) {
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
    };

    if (csrf && !customLoaded) {
      loadCustomEmoji();
    }
  }, [csrf, customLoaded, emojiShortcodeMap]);

  const getEmojiDataByShortcode = useCallback(
    shortcode => {
      return emojiShortcodeMap[shortcode.toLowerCase()] || null;
    },
    [emojiShortcodeMap]
  );

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

  const renderTextWithEmojis = useCallback(
    text => {
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
              <img
                src={emojiData.customUrl}
                alt={shortcode}
                style={{ height: '20px', width: '20px', fontSize: '20px' }}
              />
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
    },
    [getEmojiDataByShortcode]
  );

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

  return createLinksAndEmojis(kudos);
};

EnhancedKudos.propTypes = propTypes;

export default EnhancedKudos;
