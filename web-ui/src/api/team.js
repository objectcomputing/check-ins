import axios from "axios";
import { resolve, BASE_API_URL } from "./api.js";

const teamUrl = `${BASE_API_URL}/services/team`;

export const updateTeam = async (team) => {
  return await resolve(
    axios({
      method: "put",
      url: teamUrl,
      responseType: "json",
      data: team,
      withCredentials: true,
    })
  );
};

export const getTeamsByMember = async (id) => {
  return await resolve(
    axios({
      method: "get",
      url: teamUrl,
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
