import axios from "axios";
import { resolve, BASE_API_URL } from "./api.js";

export const getMemberCheckinsByPDL = async (memberId, pdlId) => {
  return await resolve(
    axios({
      method: "get",
      url: `${BASE_API_URL}/check-in/?teamMemberId=${memberId}&pdlId=${pdlId}`,
      responseType: "json",
      auth: {
        username: "ADMIN",
        password: "ADMIN",
      },
    })
  );
};

export const getCheckinByMemberId = async (id) => {
  return await resolve(
    axios({
      method: "get",
      url: `${BASE_API_URL}/check-in?teamMemberId=${id}`,
      responseType: "json",
      auth: {
        username: "ADMIN",
        password: "ADMIN",
      },
    })
  );
};

export const getCheckinByPdlId = async (id) => {
  return await resolve(
    axios({
      method: "get",
      url: `${BASE_API_URL}/check-in?pdlId=${id}`,
      responseType: "json",
      auth: {
        username: "ADMIN",
        password: "ADMIN",
      },
    })
  );
};

export const getNoteByCheckinId = async (id) => {
  return await resolve(
    axios({
      method: "get",
      url: `${BASE_API_URL}/services/checkin-note?=${id}`,
      responseType: "json",
      auth: {
        username: "ADMIN",
        password: "ADMIN",
      },
    })
  );
};
