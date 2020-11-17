import axios from "axios";
import { resolve, BASE_API_URL } from "./api.js";

const memberSkillUrl = `${BASE_API_URL}/services/member-skill`;
export const getMemberSkills = async (id) => {
  return await resolve(
    axios({
      method: "get",
      url: memberSkillUrl,
      responseType: "json",
      params: {
        memberid: id,
      },
      withCredentials: true
    })
  );
};

export const getSkillMembers = async (id) => {
  return await resolve(
    axios({
      method: "get",
      url: memberSkillUrl,
      responseType: "json",
      params: {
        skillid: id,
      },
      withCredentials: true
    })
  );
};

export const deleteMemberSkill = async (id) => {
  return await resolve(
    axios({
      method: "DELETE",
      url: `${memberSkillUrl}/${id}`,
      responseType: "json",
      withCredentials: true
    })
  );
};

export const createMemberSkill = async (memberskill) => {
  return await resolve(
    axios({
      method: "post",
      url: memberSkillUrl,
      responseType: "json",
      data: memberskill,
      withCredentials: true
    })
  );
};
