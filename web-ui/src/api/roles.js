import { resolve } from './api.js';

const roleURL = '/services/roles';

export const getAllRoles = async cookie => {
  return resolve({
    url: roleURL,
    headers: { 'X-CSRF-Header': cookie, Accept: 'application/json' }
  });
};

export const getAllMemberRoles = async cookie => {
  return resolve({
    url: roleURL + '/members',
    headers: { 'X-CSRF-Header': cookie, Accept: 'application/json' }
  });
};

export const removeMemberFromRole = async (roleId, memberId, cookie) => {
  return resolve({
    method: 'DELETE',
    url: roleURL + `/members/${roleId}/${memberId}`,
    headers: {
      'X-CSRF-Header': cookie,
      Accept: 'application/json',
      'Content-Type': 'application/json;charset=UTF-8'
    }
  });
};

export const addMemberToRole = async (roleId, memberId, cookie) => {
  return resolve({
    method: 'POST',
    url: roleURL + '/members',
    data: {
      roleId,
      memberId
    },
    headers: {
      'X-CSRF-Header': cookie,
      Accept: 'application/json',
      'Content-Type': 'application/json;charset=UTF-8'
    }
  });
};

export const addNewRole = async (role, cookie) => {
  return resolve({
    method: 'POST',
    url: roleURL,
    data: role,
    headers: {
      'X-CSRF-Header': cookie,
      Accept: 'application/json',
      'Content-Type': 'application/json;charset=UTF-8'
    }
  });
};

export const updateRole = async (role, cookie) => {
  return resolve({
    method: 'PUT',
    url: roleURL,
    data: role,
    headers: {
      'X-CSRF-Header': cookie,
      Accept: 'application/json',
      'Content-Type': 'application/json;charset=UTF-8'
    }
  });
};
