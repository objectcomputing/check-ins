import { resolve } from "./api.js";

const agendaURL = "/services/agenda-item";

export const createAgendaItem = async (agendaItem) => {
  return resolve({
    method: "post",
    url: agendaURL,
    responseType: "json",
    data: agendaItem,
  });
};

export const updateAgendaItem = async (agendaItem) => {
  return resolve({
    method: "put",
    url: agendaURL,
    responseType: "json",
    data: agendaItem,
  });
};

export const deleteAgendaItem = async (id) => {
  return resolve({
    method: "delete",
    url: `${agendaURL}/${id}`,
    responseType: "json",
  });
};

export const getAgendaItem = async (checkinId, createdById) => {
  return resolve({
    method: "get",
    url: agendaURL,
    responseType: "json",
    params: {
      checkinid: checkinId,
      createdbyid: createdById,
    },
  });
};

export const getAgendaItemById = async (id) => {
  return resolve({
    method: "get",
    url: `${agendaURL}/?id=${id}`,
    responseType: "json",
  });
};
