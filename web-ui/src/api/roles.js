import { resolve } from "./api.js";

const roleURL = "/services/roles";

export const removeUserFromRole = async (id, cookie) => {
  return resolve({
    method: "delete",
    url: roleURL + `/${id}`,
    responseType: "json",
    headers: { "X-CSRF-Header": cookie },
  });
};

export const addUserToRole = async (role, memberid, cookie) => {
  return resolve({
    method: "post",
    url: roleURL,
    data: {
      role: role,
      memberid: memberid,
    },
    responseType: "json",
    headers: { "X-CSRF-Header": cookie },
  });
};

export const addNewRole = async (role, cookie) => {
  return resolve({
    method: "post",
    url: roleURL,
    data: {
      role: role,
    },
    responseType: "json",
    headers: { "X-CSRF-Header": cookie },
  });
};
