import { resolve } from "./api.js";

const agendaURL = "/services/agenda-items";

export const createAgendaItem = async (agendaItem, cookie) => {
  return resolve({
    method: "post",
    url: agendaURL,
    responseType: "json",
    data: agendaItem,
    headers: { "X-CSRF-Header": cookie },
  });
};

export const updateAgendaItem = async (agendaItem, cookie) => {
  return resolve({
    method: "put",
    url: agendaURL,
    responseType: "json",
    data: agendaItem,
    headers: { "X-CSRF-Header": cookie },
  });
};

export const deleteAgendaItem = async (id, cookie) => {
  return resolve({
    method: "delete",
    url: `${agendaURL}/${id}`,
    responseType: "json",
    headers: { "X-CSRF-Header": cookie },
  });
};

export const getAgendaItem = async (checkinId, createdById, cookie) => {
  return resolve({
    url: agendaURL,
    responseType: "json",
    params: {
      checkinid: checkinId,
      createdbyid: createdById,
    },
    headers: { "X-CSRF-Header": cookie },
  });
};

export const getAgendaItemById = async (id, cookie) => {
  return resolve({
    url: `${agendaURL}/?id=${id}`,
    responseType: "json",
    headers: { "X-CSRF-Header": cookie },
  });
};
