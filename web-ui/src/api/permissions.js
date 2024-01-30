import { resolve } from "./api.js";

const permissionsListUrl = "/services/roles/role-permissions"

export const getPermissionsList = async (cookie) => {
    return resolve({
      url: permissionsListUrl,
      responseType: "json",
      headers: { "X-CSRF-Header": cookie },
    });
  };