import { resolve } from "./api.js";

const roleURL = "/services/roles";

export const removeUserFromRole = async (id, cookie) => {
  return resolve({
    url: roleURL + `/${id}`,
    responseType: "json",
    headers: { "X-CSRF-Header": cookie },
  });
};

export const addUserToRole = async (role, memberid, cookie) => {
  return resolve({
    url: roleURL,
    params: {
      role: role,
      memberid: memberid,
    },
    responseType: "json",
    headers: { "X-CSRF-Header": cookie },
  });
};
