import { resolve } from "./api.js";

const skillCategoryUrl = "/services/skills/categories";

export const createSkillCategory = async (skillCategory, cookie) => {
  return resolve({
    method: "post",
    url: skillCategoryUrl,
    responseType: "json",
    data: skillCategory,
    headers: { "X-CSRF-Header": cookie }
  });
};

export const getSkillCategories = async (cookie) => {
  return resolve({
    url: skillCategoryUrl,
    responseType: "json",
    headers: { "X-CSRF-Header": cookie }
  });
};