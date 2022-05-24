import { resolve } from "./api.js";

const permissionsUrl = "/services/permissions";

export const getUserPermissions = (memberId, cookie) => {
  return resolve({
    url: permissionsUrl,
    responseType: "json",
    params: {
      memberId: memberId
    },
    headers: { "X-CSRF-Header": cookie },
  });
}