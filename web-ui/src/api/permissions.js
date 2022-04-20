import { resolve } from "./api.js";

const permissionsUrl = "/services/permissions";

export const getAllRolePermissions = async (cookie) => {
  return resolve({
    url: permissionsUrl,
    responseType: "json",
    headers: { "X-CSRF-Header": cookie }
  });
};

export const addRolePermission = async (roleId, permissionId, cookie) => {
  return resolve({
    method: "post",
    url: permissionsUrl,
    data: {
      roleId,
      permissionId
    },
    responseType: "json",
    headers: { "X-CSRF-Header": cookie }
  });
};

export const removeRolePermission = async (roleId, permissionId, cookie) => {
  return resolve({
    method: "delete",
    url: permissionsUrl,
    responseType: "json",
    headers: { "X-CSRF-Header": cookie }
  });
}