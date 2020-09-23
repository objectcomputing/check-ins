import axios from "axios";
import { resolve, BASE_API_URL } from "./api.js";

const agendaURL = BASE_API_URL + "/agenda-item";

export const createAgendaItem = (agendaItem) => {
  return resolve(
    axios({
      method: "post",
      url: agendaURL,
      responseType: "json",
      data: agendaItem,
      withCredentials: true,
    })
  );
};

export const updateAgendaItem = async (agendaItem) => {
  return await resolve(
    axios({
      method: "put",
      url: `${BASE_API_URL}/services/agenda-item`,
      responseType: "json",
      data: agendaItem,
    })
  );
};

export const deleteAgendaItem = async (id) => {
  return await resolve(
    axios({
      method: "delete",
      url: `${BASE_API_URL}/services/agenda-item/${id}`,
      responseType: "json",
    })
  );
};

export const getAgendaItemByCheckinId = async (checkinId) => {
  return await resolve(
    axios({
      method: "get",
      url: `${BASE_API_URL}/services/agenda-item`,
      responseType: "json",
      data: checkinId,
      withCredentials: true,
    })
  );
};

export const getAgendaItemById = async (id) => {
  return await resolve(
    axios({
      method: "get",
      url: `${BASE_API_URL}/services/agenda-item/?id=${id}`,
      responseType: "json",
      withCredentials: true,
    })
  );
};
