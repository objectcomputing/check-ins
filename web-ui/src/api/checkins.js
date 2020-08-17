import axios from "axios";
import { resolve, BASE_API_URL } from "./api.js";

export const getMemberCheckinsByPDL = async (memberId, pdlId) => {
  return await resolve(
    axios({
      method: "get",
      url: `${BASE_API_URL}/check-in/?teamMemberId=${memberId}&pdlId=${pdlId}`,
      responseType: "json",
    })
  );
};
