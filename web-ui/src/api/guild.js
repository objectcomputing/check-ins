import { resolve } from "./api.js";

const guildUrl = `/services/guilds`;

export const getGuildsForMember = async (id, cookie) => {
  return resolve({
    url: guildUrl,
    params: {
      memberid: id,
    },
    responseType: "json",
    withCredentials: true,
    headers: { "X-CSRF-Header": cookie },
  });
};
