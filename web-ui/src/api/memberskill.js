import { resolve } from "./api.js";

const memberSkillUrl = "/services/member-skills";
const skillReportUrl = "/reports/skills"

export const getMemberSkills = async (cookie) => {
  return resolve({
    url: memberSkillUrl,
    headers: { "X-CSRF-Header": cookie, "Accept": "application/json" },
  });
};

export const getSelectedMemberSkills = async (id, cookie) => {
  return resolve({
    url: memberSkillUrl,
    params: {
      memberid: id,
    },
    headers: { "X-CSRF-Header": cookie, "Accept": "application/json" },
  });
};

export const getSkillMembers = async (id, cookie) => {
  return resolve({
    url: memberSkillUrl,
    params: {
      skillid: id,
    },
    headers: { "X-CSRF-Header": cookie, "Accept": "application/json" },
  });
};

export const deleteMemberSkill = async (id, cookie) => {
  return resolve({
    method: "DELETE",
    url: `${memberSkillUrl}/${id}`,
    headers: { "X-CSRF-Header": cookie, "Accept": "application/json" },
  });
};

export const createMemberSkill = async (memberskill, cookie) => {
  return resolve({
    method: "POST",
    url: memberSkillUrl,
    data: memberskill,
    headers: { "X-CSRF-Header": cookie, "Accept": "application/json", "Content-Type": "application/json;charset=UTF-8" },
  });
};

export const updateMemberSkill = async (memberskill, cookie) => {
  return resolve({
    method: "PUT",
    url: memberSkillUrl,
    data: memberskill,
    headers: { "X-CSRF-Header": cookie, "Accept": "application/json", "Content-Type": "application/json;charset=UTF-8" },
  });
};

export const reportSkills = async (skillLevels, cookie) => {
  return resolve({
    method: "POST",
    url: skillReportUrl,
    data: skillLevels,
    headers: { "X-CSRF-Header": cookie, "Accept": "application/json", "Content-Type": "application/json;charset=UTF-8" },
  });
};
