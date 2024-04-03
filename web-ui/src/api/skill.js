import { resolve } from "./api.js";

const skillUrl = "/services/skills";
const skillCombineUrl = "/services/skills/combine";

export const getSkills = async (cookie) => {
  return resolve({
    url: skillUrl,
    headers: { "X-CSRF-Header": cookie, "Accept": "application/json" },
  });
};

export const getSkill = async (id, cookie) => {
  return resolve({
    url: `${skillUrl}/${id}`,
    headers: { "X-CSRF-Header": cookie, "Accept": "application/json" },
  });
};

export const createSkill = async (skill, cookie) => {
  return resolve({
    method: "POST",
    url: skillUrl,
    data: skill,
    headers: { "X-CSRF-Header": cookie, "Accept": "application/json", "Content-Type": "application/json;charset=UTF-8" },
  });
};

export const updateSkill = async (skill, cookie) => {
  return resolve({
    method: "PUT",
    url: skillUrl,
    data: skill,
    headers: { "X-CSRF-Header": cookie, "Accept": "application/json", "Content-Type": "application/json;charset=UTF-8" },
  });
};

export const removeSkill = async (id, cookie) => {
  return resolve({
    method: "DELETE",
    url: skillUrl + `/${id}`,
    headers: { "X-CSRF-Header": cookie, "Accept": "application/json" },
  });
};

export const combineSkill = async (id, cookie) => {
  return resolve({
    method: "POST",
    url: skillCombineUrl,
    data: id,
    headers: { "X-CSRF-Header": cookie, "Accept": "application/json", "Content-Type": "application/json;charset=UTF-8" },
  });
};