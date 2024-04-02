import { resolve } from "./api.js";

const rolePermissionsListUrl = "/services/roles/role-permissions";

export const getRolePermissionsList = async (cookie) => {
  return resolve({
    url: rolePermissionsListUrl,
    headers: { "X-CSRF-Header": cookie, "Accept": "application/json" },
  });
};

export const postRolePermission = async (roleData, cookie) => {
  return resolve({
    method: "POST",
    url: rolePermissionsListUrl,
    data: roleData,
    headers: { "X-CSRF-Header": cookie, "Accept": "application/json", "Content-Type": "application/json;charset=UTF-8" },
  });
};

export const deleteRolePermission = async (roleData, cookie) => {
  return resolve({
    method: "DELETE",
    url: rolePermissionsListUrl,
    data: roleData,
    headers: { "X-CSRF-Header": cookie, "Accept": "application/json", "Content-Type": "application/json;charset=UTF-8" },
  });
};