import { resolve } from "./api.js";

const teamUrl = "/services/team";
const teamMemberUrl = "/services/team/member";

export const getAllTeamMembers = async () => {
  return resolve({
    method: "get",
    url: teamMemberUrl,
    responseType: "json",
  });
};

export const getMembersByTeam = async (id) => {
  return resolve({
    method: "get",
    url: teamMemberUrl,
    responseType: "json",
    params: {
      teamid: id,
    },
  });
};

export const getTeamsByMember = async (id) => {
  return resolve({
    method: "get",
    url: teamMemberUrl,
    responseType: "json",
    params: {
      memberid: id,
    },
  });
};

export const getAllTeams = async () => {
  return resolve({
    method: "get",
    url: teamUrl,
    responseType: "json",
  });
};
