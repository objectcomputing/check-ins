// import axios from "axios";
import { resolve } from "./api.js";

const memberSkillUrl = "/services/member-skill";
export const getMemberSkills = async (id) => {
  return resolve({
    method: "get",
    url: memberSkillUrl,
    responseType: "json",
    params: {
      memberid: id,
    },
  });
};

export const deleteMemberSkill = async (id) => {
  return resolve({
    method: "DELETE",
    url: `${memberSkillUrl}/${id}`,
    responseType: "json",
  });
};

export const createMemberSkill = async (memberskill) => {
  return resolve({
    method: "post",
    url: memberSkillUrl,
    responseType: "json",
    data: memberskill,
  });
};
