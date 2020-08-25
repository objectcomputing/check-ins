import axios from "axios";
import { resolve, BASE_API_URL } from "./api.js";

export const getMembersByTeam = async (id) => {
  return await resolve(
    axios({
      method: "get",
      url: `${BASE_API_URL}/services/team/member?teamid=${id}`,
      responseType: "json",
    })
  );
};

export const getTeamsByMember = async (id) => {
  return await resolve(
    axios({
      method: "get",
      url: `${BASE_API_URL}/services/team?memberid=${id}`,
      responseType: "json",
    })
  );
};
