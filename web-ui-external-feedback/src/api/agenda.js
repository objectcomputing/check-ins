import { resolve } from './api.js';

const agendaURL = '/services/agenda-items';

export const createAgendaItem = async (agendaItem, cookie) => {
  return resolve({
    method: 'POST',
    url: agendaURL,
    data: agendaItem,
    headers: {
      'X-CSRF-Header': cookie,
      Accept: 'application/json',
      'Content-Type': 'application/json;charset=UTF-8'
    }
  });
};

export const updateAgendaItem = async (agendaItem, cookie) => {
  return resolve({
    method: 'PUT',
    url: agendaURL,
    data: agendaItem,
    headers: {
      'X-CSRF-Header': cookie,
      Accept: 'application/json',
      'Content-Type': 'application/json;charset=UTF-8'
    }
  });
};

export const deleteAgendaItem = async (id, cookie) => {
  return resolve({
    method: 'DELETE',
    url: `${agendaURL}/${id}`,
    headers: { 'X-CSRF-Header': cookie, Accept: 'application/json' }
  });
};

export const getAgendaItem = async (checkinId, createdById, cookie) => {
  return resolve({
    url: agendaURL,
    params: {
      checkinid: checkinId,
      createdbyid: createdById
    },
    headers: { 'X-CSRF-Header': cookie, Accept: 'application/json' }
  });
};

export const getAgendaItemById = async (id, cookie) => {
  return resolve({
    url: `${agendaURL}/?id=${id}`,
    headers: { 'X-CSRF-Header': cookie, Accept: 'application/json' }
  });
};
