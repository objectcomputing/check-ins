import axios from "axios";
import { resolve, BASE_API_URL } from "./api.js";

export const getMembersByPDL = async (id) => {
  return await resolve(
    axios({
      method: "get",
      url: `${BASE_API_URL}/services/member-profile/?pdlId=${id}`,
      responseType: "json",
    })
  );
};

export const getMemberByEmail = async (email) => {
  return await resolve(
    axios({
      method: "get",
      url: `${BASE_API_URL}/services/member-profile/?workEmail=${email}`,
      responseType: "json",
      auth: {
        username: "ADMIN",
        password: "ADMIN",
      },
    })
  );
};

export const getMember = async (id) => {
  return await resolve(
    axios({
      method: "get",
      url: `${BASE_API_URL}/services/member-profile/${id}`,
      responseType: "json",
      auth: {
        username: "ADMIN",
        password: "ADMIN",
      },
    })
  );
};
