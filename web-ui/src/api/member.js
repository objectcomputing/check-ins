import axios from "axios";
import { resolve, BASE_API_URL } from "./api.js";

const memberProfileUrl = `${BASE_API_URL}/services/member-profile`;
export const getMembersByPDL = async (id) => {
  return await resolve(
    axios({
      method: "get",
      url: memberProfileUrl,
      responseType: "json",
      params: {
        pdlId: id,
      },
    })
  );
};

export const getMemberByEmail = async (email) => {
  return await resolve(
    axios({
      method: "get",
      url: memberProfileUrl,
      responseType: "json",
      params: {
        workEmail: email,
      },
    })
  );
};

export const getMember = async (id) => {
  return await resolve(
    axios({
      method: "get",
      url: `${memberProfileUrl}/${id}`,
      responseType: "json",
    })
  );
};

export const updateMember = async (member) => {
  return await resolve(
    axios({
      method: "put",
      url: memberProfileUrl,
      responseType: "json",
      data: member,
    })
  );
};

export const getCurrentUser = async () => {
  return await resolve(
    axios({
      method: "get",
      url: `${memberProfileUrl}/current`,
      responseType: "json",
    })
  );
};
