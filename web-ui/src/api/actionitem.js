import axios from "axios";
import { resolve, BASE_API_URL } from "./api.js";

const myAxios = axios.create({ withCredentials: true });

export const createActionItem = async (actionItem) => {
  return await resolve(
    myAxios({
      method: "post",
      url: `${BASE_API_URL}/services/action-item`,
      responseType: "json",
      data: actionItem,
    })
  );
};

export const updateActionItem = async (actionItem) => {
  return await resolve(
    myAxios({
      method: "put",
      url: `${BASE_API_URL}/services/action-item`,
      responseType: "json",
      data: actionItem,
    })
  );
};

export const deleteActionItem = async (id) => {
  return await resolve(
    myAxios({
      method: "delete",
      url: `${BASE_API_URL}/services/action-item/${id}`,
      responseType: "json",
    })
  );
};

export const findActionItem = async (checkinId, createdById) => {
  return await resolve(
    myAxios({
      method: "get",
      url: `${BASE_API_URL}/services/action-item`,
      responseType: "json",
      params: {
        checkinid: checkinId,
        createdbyid: createdById,
      },
    })
  );
};

export const getActionItem = async (id) => {
  return await resolve(
    myAxios({
      method: "get",
      url: `${BASE_API_URL}/services/action-item/?id=${id}`,
      responseType: "json",
    })
  );
};

export const createMassActionItem = async (actionItems) => {
  return await resolve(
    myAxios({
      method: "post",
      url: `${BASE_API_URL}/services/action-item/items`,
      responseType: "json",
      data: actionItems,
    })
  );
};
