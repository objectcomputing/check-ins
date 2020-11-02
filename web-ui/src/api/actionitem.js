import { resolve } from "./api.js";

export const createActionItem = async (actionItem) => {
  return resolve({
    method: "post",
    url: "/services/action-item",
    responseType: "json",
    data: actionItem,
  });
};

export const updateActionItem = async (actionItem) => {
  return resolve({
    method: "put",
    url: "/services/action-item",
    responseType: "json",
    data: actionItem,
  });
};

export const deleteActionItem = async (id) => {
  return resolve({
    method: "delete",
    url: "/services/action-item/${id}",
    responseType: "json",
  });
};

export const findActionItem = async (checkinId, createdById) => {
  return resolve({
    method: "get",
    url: "/services/action-item",
    responseType: "json",
    params: {
      checkinid: checkinId,
      createdbyid: createdById,
    },
  });
};

export const getActionItem = async (id) => {
  return resolve({
    method: "get",
    url: "/services/action-item/?id=${id}",
    responseType: "json",
  });
};

export const createMassActionItem = async (actionItems) => {
  return resolve({
    method: "post",
    url: "/services/action-item/items",
    responseType: "json",
    data: actionItems,
  });
};
