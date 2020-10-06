import axios from "axios";
import { resolve, BASE_API_URL } from "./api.js";

const myAxios = axios.create({ withCredentials: true });

const agendaURL = BASE_API_URL + "/agenda-item";

export const createAgendaItem = (agendaItem) => {
  return resolve(
    myAxios({
      method: "post",
      url: agendaURL,
      responseType: "json",
      data: agendaItem,
    })
  );
};

export const updateAgendaItem = async (agendaItem) => {
  return await resolve(
    myAxios({
      method: "put",
      url: `${BASE_API_URL}/services/agenda-item`,
      responseType: "json",
      data: agendaItem,
    })
  );
};

export const deleteAgendaItem = async (id) => {
  return await resolve(
    myAxios({
      method: "delete",
      url: `${BASE_API_URL}/services/agenda-item/${id}`,
      responseType: "json",
    })
  );
};

export const getAgendaItem = async (checkinId, createdById) => {
  return await resolve(
    myAxios({
      method: "get",
      url: `${BASE_API_URL}/services/agenda-item`,
      responseType: "json",
      params: {
        checkinid: checkinId,
        createdbyid: createdById,
      },
    })
  );
};

export const getAgendaItemById = async (id) => {
  return await resolve(
    myAxios({
      method: "get",
      url: `${BASE_API_URL}/services/agenda-item/?id=${id}`,
      responseType: "json",
    })
  );
};
