import axios from "axios";
import { resolve, BASE_API_URL } from "./api.js";

export const getMemberCheckinsByPDL = async (memberId, pdlId) => {
  return await resolve(
    axios({
      method: "get",
      url: `${BASE_API_URL}/services/check-in/?teamMemberId=${memberId}&pdlId=${pdlId}`,
      responseType: "json",
    })
  );
};

export const getCheckinByMemberId = async (id) => {
  return await resolve(
    axios({
      method: "get",
      url: `${BASE_API_URL}/services/check-in?teamMemberId=${id}`,
      responseType: "json",
    })
  );
};

export const getCheckinByPdlId = async (id) => {
  return await resolve(
    axios({
      method: "get",
      url: `${BASE_API_URL}/services/check-in?pdlId=${id}`,
      responseType: "json",
    })
  );
};
