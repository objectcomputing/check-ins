import { resolve } from "./api.js";

const memberProfileUrl = "/services/member-profile";

export const getAllMembers = async (cookie) => {
  return resolve({
    method: "get",
    url: memberProfileUrl,
    responseType: "json",
    headers: { "X-CSRF-Header": cookie },
  });
};

export const getAllPDLs = async (cookie) => {
  return resolve({
    method: "get",
    url: "/services/role?role=PDL",
    responseType: "json",
    headers: { "X-CSRF-Header": cookie },
  });
};

export const getMembersByPDL = async (id, cookie) => {
  return resolve({
    method: "get",
    url: memberProfileUrl,
    responseType: "json",
    params: {
      pdlId: id,
    },
    headers: { "X-CSRF-Header": cookie },
  });
};

export const getMemberByEmail = async (email, cookie) => {
  return resolve({
    method: "get",
    url: memberProfileUrl,
    responseType: "json",
    params: {
      workEmail: email,
    },
    headers: { "X-CSRF-Header": cookie },
  });
};

export const getMember = async (id, cookie) => {
  return resolve({
    method: "get",
    url: `${memberProfileUrl}/${id}`,
    responseType: "json",
    headers: { "X-CSRF-Header": cookie },
  });
};

export const updateMember = async (member, cookie) => {
  return resolve({
    method: "put",
    url: memberProfileUrl,
    responseType: "json",
    data: member,
    headers: { "X-CSRF-Header": cookie },
  });
};

export const getCurrentUser = async (cookie) => {
  return resolve({
    method: "get",
    url: `${memberProfileUrl}/current`,
    responseType: "json",
    headers: { "X-CSRF-Header": cookie },
  });
};

export const createMember = async (newMember, cookie) => {
  return resolve({
    method: "post",
    url: memberProfileUrl,
    responseType: "json",
    data: newMember,
    headers: { "X-CSRF-Header": cookie },
  });
};
