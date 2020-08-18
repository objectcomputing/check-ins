import axios from "axios";
import { resolve, BASE_API_URL } from "./api.js";

export const getMemberSkills = async (id) => {
  return await resolve(
    axios({
      method: "get",
      url: `${BASE_API_URL}/services/member-skill/?memberid=${id}`,
      responseType: "json",
      auth: {
        username: "ADMIN",
        password: "ADMIN",
      },
    })
  );
};

export const deleteMemberSkill = async (id) => {
  return await resolve(
    axios({
      method: "DELETE",
      url: `${BASE_API_URL}/services/member-skill/${id}`,
      responseType: "json",
      auth: {
        username: "ADMIN",
        password: "ADMIN",
      },
    })
  );
};

export const createMemberSkill = async (memberskill) => {
  return await resolve(
    axios({
      method: "post",
      url: `${BASE_API_URL}/services/member-skill`,
      responseType: "json",
      data: memberskill,
      auth: {
        username: "ADMIN",
        password: "ADMIN",
      },
    })
  );
};
