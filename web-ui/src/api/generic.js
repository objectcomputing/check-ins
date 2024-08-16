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

export const downloadJson = async (url, cookie, params) => {
  let fullURL = url;
  let separator = '?';
  for(const [key, value] of Object.entries(params)) {
    fullURL += separator + key + '=' + value;
    separator = '&';
  }
  return resolve({
    method: 'GET',
    headers: {
      'X-CSRF-Header': cookie,
      Accept: 'application/json',
      'Content-Type': 'application/json;charset=UTF-8'
    },
    url: fullURL
  });
};
