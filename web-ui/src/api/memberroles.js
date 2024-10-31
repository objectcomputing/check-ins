import { resolve } from './api.js';

const memberRolesUrl = '/services/roles/members';

export const getMemberRolesList = async cookie => {
  return resolve({
    url: memberRolesUrl,
    headers: { 'X-CSRF-Header': cookie, Accept: 'application/json' }
  });
};
