import { resolve } from "./api.js";

const memberProfileUrl = "/services/member-profile";

export const getAllMembers = async () => {
  return resolve({
    method: "get",
    url: memberProfileUrl,
    responseType: "json",
  });
};

export const getMembersByPDL = async (id) => {
  return resolve({
    method: "get",
    url: memberProfileUrl,
    responseType: "json",
    params: {
      pdlId: id,
    },
  });
};

export const getMemberByEmail = async (email) => {
  return resolve({
    method: "get",
    url: memberProfileUrl,
    responseType: "json",
    params: {
      workEmail: email,
    },
  });
};

export const getMember = async (id) => {
  return resolve({
    method: "get",
    url: `${memberProfileUrl}/${id}`,
    responseType: "json",
  });
};

export const updateMember = async (member) => {
  return resolve({
    method: "put",
    url: memberProfileUrl,
    responseType: "json",
    data: member,
  });
};

export const getCurrentUser = async () => {
  return resolve({
    method: "get",
    url: `${memberProfileUrl}/current`,
    responseType: "json",
  });
};
