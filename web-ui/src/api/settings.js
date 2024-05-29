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
