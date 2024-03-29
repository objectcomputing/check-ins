import { resolve } from "./api.js";

const rolePermissionsListUrl = "/services/roles/role-permissions";

export const getRolePermissionsList = async (cookie) => {
  return resolve({
    url: rolePermissionsListUrl,
    responseType: "json",
    headers: { "X-CSRF-Header": cookie },
  });
};

export const postRolePermission = async (roleData, cookie) => {
  return resolve({
    method: "post",
    url: rolePermissionsListUrl,
    responseType: "json",
    data: roleData,
    headers: { "X-CSRF-Header": cookie },
  });
};

export const deleteRolePermission = async (roleData, cookie) => {
  return resolve({
    method: "delete",
    url: rolePermissionsListUrl,
    responseType: "json",
    data: roleData,
    headers: { "X-CSRF-Header": cookie },
  });
};