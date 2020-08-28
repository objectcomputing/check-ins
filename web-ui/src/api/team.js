import axios from "axios";
import { resolve, BASE_API_URL } from "./api.js";

const teamMemberUrl = `${BASE_API_URL}/services/team/member`;
export const getMembersByTeam = async (id) => {
  return await resolve(
    axios({
      method: "get",
      url: teamMemberUrl,
      responseType: "json",
      params: {
        teamid: id,
      },
      withCredentials: true,
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
      withCredentials: true,
    })
  );
};
