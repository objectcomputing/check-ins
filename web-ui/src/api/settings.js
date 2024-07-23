import { resolve } from './api.js';

const settingsURL = '/services/settings/';

export const getSettingValue = async (settingName, cookie) => {
  return resolve({
    method: 'GET',
    url: settingsURL + settingName,
    headers: {
      'X-CSRF-Header': cookie,
      Accept: 'application/json',
      'Content-Type': 'application/json;charset=UTF-8'
    }
  });
};

export const getAll = async cookie => {
  return resolve({
    method: 'GET',
    url: settingsURL,
    headers: {
      'X-CSRF-Header': cookie,
      Accept: 'application/json',
      'Content-Type': 'application/json;charset=UTF-8'
    }
  });
};

export const getAllOptions = async cookie => {
  return resolve({
    method: 'GET',
    url: settingsURL + `options`,
    headers: {
      'X-CSRF-Header': cookie,
      Accept: 'application/json',
      'Content-Type': 'application/json;charset=UTF-8'
    }
  });
};

export const updateSetting = async (name, value, cookie) => {
  return resolve({
    method: 'PUT',
    url: settingsURL,
    data: {
      name,
      value
    },
    headers: {
      'X-CSRF-Header': cookie,
      Accept: 'application/json',
      'Content-Type': 'application/json;charset=UTF-8'
    }
  });
};