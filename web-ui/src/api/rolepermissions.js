import { resolve } from "./api.js";

const rolePermissionsListUrl = "/services/roles/role-permissions"

export const getRolePermissionsList = async (cookie) => {
    return resolve({
      url: rolePermissionsListUrl,
      responseType: "json",
      headers: { "X-CSRF-Header": cookie },
    });
  };