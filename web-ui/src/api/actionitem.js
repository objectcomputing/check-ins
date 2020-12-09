import { resolve } from "./api.js";

export const createActionItem = async (actionItem, cookie) => {
  return resolve({
    method: "post",
    url: "/services/action-item",
    responseType: "json",
    data: actionItem,
    headers: { "X-CSRF-Header": cookie },
  });
};

export const updateActionItem = async (actionItem, cookie) => {
  return resolve({
    method: "put",
    url: "/services/action-item",
    responseType: "json",
    data: actionItem,
    headers: { "X-CSRF-Header": cookie },
  });
};

export const deleteActionItem = async (id, cookie) => {
  return resolve({
    method: "delete",
    url: `/services/action-item/${id}`,
    responseType: "json",
    headers: { "X-CSRF-Header": cookie },
  });
};

export const findActionItem = async (checkinId, createdById, cookie) => {
  return resolve({
    url: "/services/action-item",
    responseType: "json",
    params: {
      checkinid: checkinId,
      createdbyid: createdById,
    },
    headers: { "X-CSRF-Header": cookie },
  });
};

export const getActionItem = async (id, cookie) => {
  return resolve({
    url: `/services/action-item/?id=${id}`,
    responseType: "json",
    headers: { "X-CSRF-Header": cookie },
  });
};

