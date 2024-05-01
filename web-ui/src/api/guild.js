import { resolve } from './api.js';

const guildUrl = `/services/guilds`;
const guildMemberUrl = `/services/guilds/members`;

export const getAllGuildMembers = async cookie => {
  return resolve({
    url: guildMemberUrl,
    headers: { 'X-CSRF-Header': cookie, Accept: 'application/json' }
  });
};

export const getMembersByGuild = async (id, cookie) => {
  return resolve({
    url: guildMemberUrl,
    params: {
      guildid: id
    },
    headers: { 'X-CSRF-Header': cookie, Accept: 'application/json' }
  });
};

export const updateGuild = async (guild, cookie) => {
  console.log('guild.js updateGuild: guild =', guild);
  return resolve({
    method: 'PUT',
    url: guildUrl,
    data: guild,
    headers: {
      'X-CSRF-Header': cookie,
      Accept: 'application/json',
      'Content-Type': 'application/json;charset=UTF-8'
    }
  });
};

export const getGuildsForMember = async (id, cookie) => {
  return resolve({
    url: guildUrl,
    params: {
      memberid: id
    },
    headers: { 'X-CSRF-Header': cookie, Accept: 'application/json' }
  });
};

export const getAllGuilds = async cookie => {
  return resolve({
    url: guildUrl,
    headers: { 'X-CSRF-Header': cookie, Accept: 'application/json' }
  });
};

export const createGuild = async (guild, cookie) => {
  console.log('guild.js createGuild: guild =', guild);
  return resolve({
    method: 'POST',
    url: guildUrl,
    data: guild,
    headers: {
      'X-CSRF-Header': cookie,
      Accept: 'application/json',
      'Content-Type': 'application/json;charset=UTF-8'
    }
  });
};

export const addGuildMember = async (id, isLead, guildId, cookie) => {
  return resolve({
    method: 'POST',
    url: guildMemberUrl,
    data: { memberId: id, lead: isLead, guildId: guildId },
    headers: {
      'X-CSRF-Header': cookie,
      Accept: 'application/json',
      'Content-Type': 'application/json;charset=UTF-8'
    }
  });
};

export const deleteGuildMember = async (id, cookie) => {
  return resolve({
    method: 'DELETE',
    url: guildMemberUrl + `/${id}`,
    headers: { 'X-CSRF-Header': cookie, Accept: 'application/json' }
  });
};

export const deleteGuild = async (id, cookie) => {
  return resolve({
    method: 'DELETE',
    url: `${guildUrl}/${id}`,
    headers: { 'X-CSRF-Header': cookie, Accept: 'application/json' }
  });
};
