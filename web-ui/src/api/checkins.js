import axios from "axios";
import { resolve, BASE_API_URL } from "./api.js";

const checkinsUrl = `${BASE_API_URL}/services/check-in`;
export const getMemberCheckinsByPDL = async (memberId, pdlId) => {
  return await resolve(
    axios({
      method: "get",
      url: checkinsUrl,
      responseType: "json",
      params: {
        teamMemberId: memberId,
        pdlId: pdlId,
      },
    })
  );
};

export const getCheckinByMemberId = async (id) => {
  return await resolve(
    axios({
      method: "get",
      url: checkinsUrl,
      responseType: "json",
      params: {
        teamMemberId: id,
      },
    })
  );
};

export const getCheckinByPdlId = async (id) => {
  return await resolve(
    axios({
      method: "get",
      url: checkinsUrl,
      responseType: "json",
      params: {
        pdlId: id,
      },
      withCredentials: true,
    })
  );
};
