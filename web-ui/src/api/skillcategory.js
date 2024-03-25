import { resolve } from "./api.js";

const skillCategoryUrl = "/services/skills/categories";
const skillCategorySkillUrl = "/services/skills/category-skills";
const skillRecordsUrl = "/services/skills/records";

export const createSkillCategory = async (skillCategory, cookie) => {
  return resolve({
    method: "post",
    url: skillCategoryUrl,
    responseType: "json",
    data: skillCategory,
    headers: { "X-CSRF-Header": cookie }
  });
};

export const updateSkillCategory = async (skillCategory, cookie) => {
  return resolve({
    method: "put",
    url: skillCategoryUrl,
    responseType: "json",
    data: skillCategory,
    headers: { "X-CSRF-Header": cookie }
  });
}

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

export const deleteSkillCategory = async (categoryId, cookie) => {
  return resolve({
    method: "delete",
    url: `${skillCategoryUrl}/${categoryId}`,
    headers: {"X-CSRF-Header": cookie}
  });
}

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

export const createSkillCategorySkills = async (categoryId, skillIds, cookie) => {
  const skillIdList = [...skillIds];
  const promises = skillIdList.map((skillId) => {
    return createSkillCategorySkill(categoryId, skillId, cookie);
  });

  return Promise.all(promises);
}

export const deleteSkillCategorySkill = async (categoryId, skillId, cookie) => {
  return resolve({
    method: "delete",
    url: skillCategorySkillUrl,
    data: {
      skillCategoryId: categoryId,
      skillId: skillId
    },
    headers: { "X-CSRF-Header": cookie }
  });
}

export const getSkillsCsv = async (cookie) => {
  return resolve({
    url: `${skillRecordsUrl}/csv`,
    responseType: "blob",
    headers: { "X-CSRF-Header": cookie, "Accept": "text/csv" }
  });
}