import { resolve } from "./api.js";

const skillCategoryUrl = "/services/skills/categories";
const skillCategorySkillUrl = "/services/skills/category-skills";

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
    url: `${skillCategoryUrl}/with-skills`,
    responseType: "json",
    headers: { "X-CSRF-Header": cookie }
  });
};

export const getSkillCategory = async (categoryId, cookie) => {
  return resolve({
    url: `${skillCategoryUrl}/${categoryId}`,
    responseType: "json",
    headers: {
      "X-CSRF-Header": cookie,
      'Content-Type': 'application/json'
    }
  });
};

export const createSkillCategorySkill = async (categoryId, skillId, cookie) => {
  return resolve({
    method: "post",
    url: skillCategorySkillUrl,
    responseType: "json",
    data: {
      skillCategoryId: categoryId,
      skillId: skillId
    },
    headers: { "X-CSRF-Header": cookie }
  });
}