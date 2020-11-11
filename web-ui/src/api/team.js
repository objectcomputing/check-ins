import { resolve } from "./api.js";

const teamUrl = "/services/team";
const teamMemberUrl = "/services/team/member";

export const getAllTeamMembers = async () => {
  return resolve({
    url: teamMemberUrl,
    responseType: "json",
    withCredentials: true,
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

export const createTeam = async (team, cookie) => {
  return resolve({
    method: "post",
    url: teamUrl,
    responseType: "json",
    data: team,
    headers: { "X-CSRF-Header": cookie },
  });
};

export const addTeamMember = async (member, isLead, teamid, cookie) => {
  return resolve({
    method: "post",
    url: teamMemberUrl,
    responseType: "json",
    data: { memberid: member.id, lead: isLead, teamid: teamid },
    headers: { "X-CSRF-Header": cookie },
  });
};
