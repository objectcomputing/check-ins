import axios from "axios";
import { resolve, BASE_API_URL } from "./api.js";

const teamUrl = `${BASE_API_URL}/services/team`;
const teamMemberUrl = `${BASE_API_URL}/services/team/member`;
export const getAllTeamMembers = async () => {
  return await resolve(
    axios({
      method: "get",
      url: teamMemberUrl,
      responseType: "json",
      withCredentials: true
    })
  );
};

export const getMembersByTeam = async (id) => {
  return await resolve(
    axios({
      method: "get",
      url: teamMemberUrl,
      responseType: "json",
      params: {
        teamid: id,
      },
      withCredentials: true
    })
  );
};

export const getTeamsByMember = async (id) => {
  return await resolve(
    axios({
      method: "get",
      url: teamMemberUrl,
      responseType: "json",
      params: {
        memberid: id,
      },
      withCredentials: true
    })
  );
};

export const getAllTeams = async () => {
  return await resolve(
    axios({
      method: "get",
      url: teamUrl,
      responseType: "json",
      withCredentials: true
    })
  )
};
