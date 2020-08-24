import axios from "axios";
import { resolve, BASE_API_URL } from "./api.js";

export const getSkills = async () => {
  return await resolve(
    axios({
      method: "get",
      url: `${BASE_API_URL}/services/skill/all`,
      responseType: "json",
    })
  );
};

export const getSkill = async (id) => {
  return await resolve(
    axios({
      method: "get",
      url: `${BASE_API_URL}/services/skill/${id}`,
      responseType: "json",
    })
  );
};

export const createSkill = async (skill) => {
  return await resolve(
    axios({
      method: "post",
      url: `${BASE_API_URL}/services/skill`,
      responseType: "json",
      data: skill,
    })
  );
};
