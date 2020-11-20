import { resolve } from "./api.js";

const teamUrl = "/services/team";
const teamMemberUrl = "/services/team/member";

export const getAllTeamMembers = async (cookie) => {
  return resolve({
    url: teamMemberUrl,
    responseType: "json",
    headers: { "X-CSRF-Header": cookie },
  });
};

export const getMembersByTeam = async (id, cookie) => {
  return resolve({
    url: teamMemberUrl,
    responseType: "json",
    params: {
      teamid: id,
    },
    headers: { "X-CSRF-Header": cookie },
  });
};

export const getTeamsByMember = async (id, cookie) => {
  return resolve({
    url: teamMemberUrl,
    responseType: "json",
    params: {
      memberid: id,
    },
    headers: { "X-CSRF-Header": cookie },
  });
};

export const getAllTeams = async (cookie) => {
  return resolve({
    url: teamUrl,
    responseType: "json",
    headers: { "X-CSRF-Header": cookie },
  });
};

export const createTeam = async (cookie) => {
  return resolve({
    method: "post",
    url: teamUrl,
    responseType: "json",
    headers: { "X-CSRF-Header": cookie },
  });
};
