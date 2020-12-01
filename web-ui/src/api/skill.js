import { resolve } from "./api.js";

const skillUrl = "/services/skill";

export const getSkills = async (cookie) => {
  return resolve({
    method: "get",
    url: skillUrl,
    responseType: "json",
    headers: { "X-CSRF-Header": cookie },
  });
};

export const getSkill = async (id, cookie) => {
  return resolve({
    url: `${skillUrl}/${id}`,
    responseType: "json",
    headers: { "X-CSRF-Header": cookie },
  });
};

export const createSkill = async (skill, cookie) => {
  return resolve({
    method: "post",
    url: skillUrl,
    responseType: "json",
    data: skill,
    headers: { "X-CSRF-Header": cookie },
  });
};

export const updateSkill = async (skill, cookie) => {
  return resolve({
    method: "put",
    url: skillUrl,
    responseType: "json",
    data: skill,
    headers: { "X-CSRF-Header": cookie },
  });
};

export const removeSkill = async (id, cookie) => {
  return resolve({
    method: "delete",
    url: skillUrl,
    responseType: "json",
    data: id,
    headers: { "X-CSRF-Header": cookie },
  });
};
