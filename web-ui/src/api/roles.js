import { resolve } from "./api.js";

const roleURL = "/services/roles";

export const getAllRoles = async (cookie) => {
  return resolve({
    url: roleURL,
    responseType: "json",
    headers: { "X-CSRF-Header": cookie },
  });
};


export const getAllUserRoles = async (cookie) => {
  return resolve({
    url: roleURL + '/members',
    responseType: "json",
    headers: { "X-CSRF-Header": cookie },
  });
};

export const removeUserFromRole = async (roleId, memberId, cookie) => {
  return resolve({
    method: "delete",
    url: roleURL + `/members/${roleId}/${memberId}`,
    responseType: "json",
    headers: { "X-CSRF-Header": cookie },
  });
};

export const addUserToRole = async (roleId, memberId, cookie) => {
  return resolve({
    method: "post",
    url: roleURL + '/members',
    data: {
      roleId,
      memberId,
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
