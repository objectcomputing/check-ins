import { resolve } from "./api.js";

const memberProfileUrl = "/services/member-profiles";
const csvReportUrl = "/services/reports/member/csv";


export const getAllMembers = async (cookie) => {
  return resolve({
    url: memberProfileUrl,
    responseType: "json",
    headers: { "X-CSRF-Header": cookie },
  });
};

export const getAllTerminatedMembers = async (cookie) => {
  return resolve({
    url: memberProfileUrl,
    responseType: "json",
    params: {
      terminated: true,
    },
    headers: { "X-CSRF-Header": cookie },
  });
};

export const getAllPDLs = async (cookie) => {
  return resolve({
    url: "/services/roles?role=PDL",
    responseType: "json",
    headers: { "X-CSRF-Header": cookie },
  });
};

export const getMembersByPDL = async (id, cookie) => {
  return resolve({
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

export const deleteMember = async (id, cookie) => {
  return resolve({
    method: "delete",
    url: `${memberProfileUrl}/${id}`,
    responseType: "json",
    headers: { "X-CSRF-Header": cookie },
  });
};

export const reportMemberCsv = async (cookie) => {
  return resolve({
    url: csvReportUrl,
    responseType: 'blob',
    headers: { "X-CSRF-Header": cookie, 'Accept': 'text/csv' },
  });
};