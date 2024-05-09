import { resolve } from './api.js';

const actionItemUrl = '/services/action-items';

export const createActionItem = async (actionItem, cookie) => {
  return resolve({
    method: 'post',
    url: actionItemUrl,
    data: actionItem,
    headers: {
      'X-CSRF-Header': cookie,
      Accept: 'application/json',
      'Content-Type': 'application/json;charset=UTF-8'
    }
  });
};

export const updateActionItem = async (actionItem, cookie) => {
  return resolve({
    method: 'put',
    url: actionItemUrl,
    data: actionItem,
    headers: {
      'X-CSRF-Header': cookie,
      Accept: 'application/json',
      'Content-Type': 'application/json;charset=UTF-8'
    }
  });
};

export const deleteActionItem = async (id, cookie) => {
  return resolve({
    method: 'delete',
    url: `${actionItemUrl}/${id}`,
    headers: { 'X-CSRF-Header': cookie, Accept: 'application/json' }
  });
};

export const findActionItem = async (checkinId, createdById, cookie) => {
  return resolve({
    url: actionItemUrl,
    params: {
      checkinid: checkinId,
      createdbyid: createdById
    },
    headers: { 'X-CSRF-Header': cookie, Accept: 'application/json' }
  });
};

export const getActionItem = async (id, cookie) => {
  return resolve({
    url: `${actionItemUrl}/?id=${id}`,
    headers: { 'X-CSRF-Header': cookie, Accept: 'application/json' }
  });
};
