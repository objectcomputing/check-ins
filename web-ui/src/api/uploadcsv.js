import { resolve } from './api.js';

export const uploadFile = async (url, cookie, file) => {
  return resolve({
    headers: {
      'X-CSRF-Header': cookie,
      Accept: 'application/json'
    },
    method: 'POST',
    url: url,
    body: file
  });
};
