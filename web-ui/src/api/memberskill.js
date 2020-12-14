// import axios from "axios";
import { resolve } from "./api.js";

const memberSkillUrl = "/services/member-skill";

export const getMemberSkills = async (cookie) => {
  return resolve({
    url: memberSkillUrl,
    responseType: "json",
    headers: { "X-CSRF-Header": cookie },
  });
};

export const getSkillMembers = async (id, cookie) => {
  return resolve({
    url: memberSkillUrl,
    responseType: "json",
    params: {
      skillid: id,
    },
    headers: { "X-CSRF-Header": cookie },
  });
};

export const deleteMemberSkill = async (id, cookie) => {
  return resolve({
    method: "DELETE",
    url: `${memberSkillUrl}/${id}`,
    responseType: "json",
    headers: { "X-CSRF-Header": cookie },
  });
};

export const createMemberSkill = async (memberskill, cookie) => {
  return resolve({
    method: "post",
    url: memberSkillUrl,
    responseType: "json",
    data: memberskill,
    headers: { "X-CSRF-Header": cookie },
  });
};
