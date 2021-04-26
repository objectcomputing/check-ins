import { resolve } from "./api.js";

const actionItemUrl = "/services/action-items"

export const createActionItem = async (actionItem, cookie) => {
  return resolve({
    method: "post",
    url: actionItemUrl,
    responseType: "json",
    data: actionItem,
    headers: { "X-CSRF-Header": cookie },
  });
};

export const updateActionItem = async (actionItem, cookie) => {
  return resolve({
    method: "put",
    url: actionItemUrl,
    responseType: "json",
    data: actionItem,
    headers: { "X-CSRF-Header": cookie },
  });
};

export const deleteActionItem = async (id, cookie) => {
  return resolve({
    method: "delete",
    url: `${actionItemUrl}/${id}`,
    responseType: "json",
    headers: { "X-CSRF-Header": cookie },
  });
};

export const findActionItem = async (checkinId, createdById, cookie) => {
  return resolve({
    url: actionItemUrl,
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
    url: `${actionItemUrl}/?id=${id}`,
    responseType: "json",
    headers: { "X-CSRF-Header": cookie },
  });
};

