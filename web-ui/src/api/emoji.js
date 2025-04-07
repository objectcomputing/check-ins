import { resolve } from './api.js';

const emojiUrl = '/services/slack/emoji';

export const getCustomEmoji = async (cookie) => {
  return resolve({
    url: emojiUrl,
    responseType: 'json',
    headers: { 'X-CSRF-Header': cookie, Accept: 'application/json' }
  });
};
