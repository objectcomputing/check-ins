import { resolve } from './api.js';

export const uploadData = (url, cookie, file) => {
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

export const downloadData = (url, cookie, params) => {
  return resolve({
    method: 'GET',
    params: params,
    headers: {
      'X-CSRF-Header': cookie,
      Accept: 'application/json',
    },
    url: url
  });
};

export const initiate = (url, cookie, params) => {
  return resolve({
    method: 'GET',
    params: params,
    headers: {
      'X-CSRF-Header': cookie,
      Accept: 'application/json',
    },
    url: url
  });
};
