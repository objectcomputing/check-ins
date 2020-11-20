import { resolve } from "./api.js";

const teamUrl = `/services/team`;

export const updateTeam = async (team, cookie) => {
  return await resolve({
      method: "put",
      url: teamUrl,
      responseType: "json",
      data: team,
      withCredentials: true,
      headers: { "X-CSRF-Header": cookie },
    });
};

export const getTeamsByMember = async (id, cookie) => {
  return await resolve({
      method: "get",
      url: teamUrl,
      responseType: "json",
      params: {
        memberid: id,
      },
      withCredentials: true,
      headers: { "X-CSRF-Header": cookie },
    });
};

export const getAllTeams = async (cookie) => {
  return resolve({
    method: "get",
    url: teamUrl,
    responseType: "json",
    headers: { "X-CSRF-Header": cookie },
  });
};
