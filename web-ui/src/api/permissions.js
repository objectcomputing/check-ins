import { resolve } from "./api.js";

const permissionsUrl = "/services/permissions";

export const getUserPermissions = (memberId, cookie) => {
  return resolve({
    url: `${permissionsUrl}/${memberId}`,
    responseType: "json",
    headers: { "X-CSRF-Header": cookie },
  });
}