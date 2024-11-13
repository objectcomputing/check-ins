import { resolve } from './api.js';

const settingsURL = '/services/settings';

export const getAllOptions = async cookie => {
  return resolve({
    method: 'GET',
    url: settingsURL + `/options`,
    headers: {
      'X-CSRF-Header': cookie,
      Accept: 'application/json',
      'Content-Type': 'application/json;charset=UTF-8'
    }
  });
};

export const putOption = async (option, cookie) => {
  return resolve({
    method: 'PUT',
    url: settingsURL,
    headers: {
      'X-CSRF-Header': cookie,
      Accept: 'application/json',
      'Content-Type': 'application/json;charset=UTF-8'
    },
    data: option,
  });
};

export const postOption = async (option, cookie) => {
  return resolve({
    method: 'POST',
    url: settingsURL,
    headers: {
      'X-CSRF-Header': cookie,
      Accept: 'application/json',
      'Content-Type': 'application/json;charset=UTF-8'
    },
    data: option,
  });
};
