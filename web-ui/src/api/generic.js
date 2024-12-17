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
    method: 'POST',
    headers: {
      'X-CSRF-Header': cookie,
      Accept: 'application/json',
      'Content-Type': 'application/json;charset=UTF-8'
    },
    url: url,
    data: params,
  });
};
