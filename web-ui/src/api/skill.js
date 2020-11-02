import { resolve } from "./api.js";

const skillUrl = "/services/skill";
export const getSkills = async () => {
  return resolve({
    method: "get",
    url: skillUrl,
    responseType: "json",
  });
};

export const getSkill = async (id) => {
  return resolve({
    method: "get",
    url: `${skillUrl}/${id}`,
    responseType: "json",
  });
};

export const createSkill = async (skill) => {
  return resolve({
    method: "post",
    url: skillUrl,
    responseType: "json",
    data: skill,
  });
};
