import { resolve } from "./api.js";

const permissionsUrl = "/services/permissions";
const rolePermissionsUrl = "/services/roles/role-permissions";

export const getAllPermissions = async (cookie) => {
  return resolve({
    url: permissionsUrl,
    responseType: "json",
    headers: { "X-CSRF-Header": cookie }
  })
}

export const getUserPermissions = (memberId, cookie) => {
  return resolve({
    url: `${permissionsUrl}/${memberId}`,
    responseType: "json",
    headers: { "X-CSRF-Header": cookie },
  });
}

export const getAllRolePermissions = async (cookie) => {
  return resolve({
    url: rolePermissionsUrl,
    responseType: "json",
    headers: { "X-CSRF-Header": cookie }
  });
};

export const addRolePermission = async (roleId, permissionId, cookie) => {
  return resolve({
    method: "post",
    url: rolePermissionsUrl,
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
    url: rolePermissionsUrl,
    responseType: "json",
    headers: { "X-CSRF-Header": cookie }
  });
}