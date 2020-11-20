// import axios from "axios";
import { resolve } from "./api.js";

const memberSkillUrl = "/services/member-skill";

export const getMemberSkills = async (id, cookie) => {
  return resolve({
    method: "get",
    url: memberSkillUrl,
    responseType: "json",
    params: {
      memberid: id,
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
