import axios from "axios";
import { resolve, BASE_API_URL } from "./api.js";

const skillUrl = `${BASE_API_URL}/services/skill`;
export const getSkills = async () => {
  return await resolve(
    axios({
      method: "get",
      url: `${skillUrl}/all`,
      responseType: "json",
      withCredentials: true,
    })
  );
};

export const getSkill = async (id) => {
  return await resolve(
    axios({
      method: "get",
      url: `${skillUrl}/${id}`,
      responseType: "json",
      withCredentials: true,
    })
  );
};

export const createSkill = async (skill) => {
  return await resolve(
    axios({
      method: "post",
      url: skillUrl,
      responseType: "json",
      data: skill,
      withCredentials: true,
    })
  );
};
