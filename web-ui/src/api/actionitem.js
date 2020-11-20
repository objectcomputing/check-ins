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
    method: "get",
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
    method: "get",
    url: `/services/action-item/?id=${id}`,
    responseType: "json",
    headers: { "X-CSRF-Header": cookie },
  });
};

export const createMassActionItem = async (actionItems, cookie) => {
  return resolve({
    method: "post",
    url: "/services/action-item/items",
    responseType: "json",
    data: actionItems,
    headers: { "X-CSRF-Header": cookie },
  });
};
