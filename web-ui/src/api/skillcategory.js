import { resolve } from './api.js';

const skillCategoryUrl = '/services/skills/categories';
const skillCategorySkillUrl = '/services/skills/category-skills';
const skillRecordsUrl = '/services/skills/records';

export const createSkillCategory = async (skillCategory, cookie) => {
  return resolve({
    method: 'POST',
    url: skillCategoryUrl,
    data: skillCategory,
    headers: {
      'X-CSRF-Header': cookie,
      Accept: 'application/json',
      'Content-Type': 'application/json;charset=UTF-8'
    }
  });
};

export const updateSkillCategory = async (skillCategory, cookie) => {
  return resolve({
    method: 'PUT',
    url: skillCategoryUrl,
    data: skillCategory,
    headers: {
      'X-CSRF-Header': cookie,
      Accept: 'application/json',
      'Content-Type': 'application/json;charset=UTF-8'
    }
  });
};

export const getSkillCategories = async cookie => {
  return resolve({
    url: `${skillCategoryUrl}/with-skills`,
    headers: { 'X-CSRF-Header': cookie, Accept: 'application/json' }
  });
};

export const getSkillCategory = async (categoryId, cookie) => {
  return resolve({
    url: `${skillCategoryUrl}/${categoryId}`,
    headers: { 'X-CSRF-Header': cookie, Accept: 'application/json' }
  });
};

export const deleteSkillCategory = async (categoryId, cookie) => {
  return resolve({
    method: 'DELETE',
    url: `${skillCategoryUrl}/${categoryId}`,
    headers: { 'X-CSRF-Header': cookie, Accept: 'application/json' }
  });
};

export const createSkillCategorySkill = async (categoryId, skillId, cookie) => {
  return resolve({
    method: 'POST',
    url: skillCategorySkillUrl,
    data: {
      skillCategoryId: categoryId,
      skillId: skillId
    },
    headers: {
      'X-CSRF-Header': cookie,
      Accept: 'application/json',
      'Content-Type': 'application/json;charset=UTF-8'
    }
  });
};

export const createSkillCategorySkills = async (
  categoryId,
  skillIds,
  cookie
) => {
  const skillIdList = [...skillIds];
  const promises = skillIdList.map(skillId => {
    return createSkillCategorySkill(categoryId, skillId, cookie);
  });

  return Promise.all(promises);
};

export const deleteSkillCategorySkill = async (categoryId, skillId, cookie) => {
  return resolve({
    method: 'DELETE',
    url: skillCategorySkillUrl,
    data: {
      skillCategoryId: categoryId,
      skillId: skillId
    },
    headers: {
      'X-CSRF-Header': cookie,
      Accept: 'application/json',
      'Content-Type': 'application/json;charset=UTF-8'
    }
  });
};

export const getSkillsCsv = async cookie => {
  return resolve({
    url: `${skillRecordsUrl}/csv`,
    headers: { 'X-CSRF-Header': cookie, Accept: 'text/csv' }
  });
};
