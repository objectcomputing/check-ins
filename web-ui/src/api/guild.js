import { resolve } from "./api.js";

const guildUrl = `/services/guilds`;
const guildMemberUrl = `/services/guilds/members`;

export const getAllGuildMembers = async (cookie) => {
  return resolve({
    url: guildMemberUrl,
    responseType: "json",
    withCredentials: true,
    headers: { "X-CSRF-Header": cookie },
  });
};

export const getMembersByGuild = async (id, cookie) => {
  return resolve({
    url: guildMemberUrl,
    responseType: "json",
    params: {
      guildid: id,
    },
    withCredentials: true,
    headers: { "X-CSRF-Header": cookie },
  });
};

export const updateGuild = async (guild, cookie) => {
  return resolve({
    method: "put",
    url: guildUrl,
    responseType: "json",
    data: guild,
    withCredentials: true,
    headers: { "X-CSRF-Header": cookie },
  });
};

export const getGuildsForMember = async (id, cookie) => {
  return resolve({
    url: guildUrl,
    responseType: "json",
    params: {
      memberid: id,
    },
    withCredentials: true,
    headers: { "X-CSRF-Header": cookie },
  });
};

export const getAllGuilds = async (cookie) => {
  return resolve({
    url: guildUrl,
    responseType: "json",
    headers: { "X-CSRF-Header": cookie },
  });
};

export const createGuild = async (guild, cookie) => {
  return resolve({
    method: "post",
    url: guildUrl,
    responseType: "json",
    data: guild,
    headers: { "X-CSRF-Header": cookie },
  });
};

export const addGuildMember = async (id, isLead, guildId, cookie) => {
  return resolve({
    method: "post",
    url: guildMemberUrl,
    responseType: "json",
    data: { memberId: id, lead: isLead, guildId: guildId },
    headers: { "X-CSRF-Header": cookie },
  });
};

export const deleteGuildMember = async (id, cookie) => {
  return resolve({
    method: "delete",
    url: guildMemberUrl + `/${id}`,
    responseType: "json",
    headers: { "X-CSRF-Header": cookie },
  });
};

export const deleteGuild = async (id, cookie) => {
  return resolve({
    method: "delete",
    url: `${guildUrl}/${id}`,
    responseType: "json",
    headers: { "X-CSRF-Header": cookie },
  });
};
