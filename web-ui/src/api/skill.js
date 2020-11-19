import axios from "axios";
import { resolve, BASE_API_URL } from "./api.js";

const skillUrl = `${BASE_API_URL}/services/skill`;
export const getSkills = async () => {
  return await resolve(
    axios({
      method: "get",
      url: skillUrl,
      responseType: "json",
      withCredentials: true,
    })
  );
};
export const getPendingSkills = async () => {
  return await resolve(
    axios({
      method: "get",
      url: skillUrl + "?pending=true",
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

export const updateSkill = async (skill) => {
  return await resolve(
    axios({
      method: "put",
      url: skillUrl,
      responseType: "json",
      data: skill,
      withCredentials: true,
    })
  );
};

export const removeSkill = async (id) => {
  return await resolve(
    axios({
      method: "delete",
      url: skillUrl,
      responseType: "json",
      data: id,
      withCredentials: true,
    })
  );
};