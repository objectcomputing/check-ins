import { resolve } from './api.js';

const permissionsListUrl = '/services/permissions';

export const getPermissionsList = async cookie => {
  return resolve({
    url: permissionsListUrl,
    headers: { 'X-CSRF-Header': cookie, Accept: 'application/json' }
  });
};
