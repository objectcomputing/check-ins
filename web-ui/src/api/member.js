import { resolve } from "./api.js";

const memberProfileUrl = "/services/member-profiles";
const memberProfileReportUrl = "/services/reports/member";


export const getAllMembers = async (cookie) => {
  return resolve({
    url: memberProfileUrl,
    headers: { "X-CSRF-Header": cookie, "Accept": "application/json" },
  });
};

export const getAllTerminatedMembers = async (cookie) => {
  return resolve({
    url: memberProfileUrl,
    params: {
      terminated: true,
    },
    headers: { "X-CSRF-Header": cookie, "Accept": "application/json" },
  });
};

export const getAllPDLs = async (cookie) => {
  return resolve({
    url: "/services/roles?role=PDL",
    headers: { "X-CSRF-Header": cookie, "Accept": "application/json" },
  });
};

export const getMembersByPDL = async (id, cookie) => {
  return resolve({
    url: memberProfileUrl,
    params: {
      pdlId: id,
    },
    headers: { "X-CSRF-Header": cookie, "Accept": "application/json" },
  });
};

export const getMemberByEmail = async (email, cookie) => {
  return resolve({
    url: memberProfileUrl,
    params: {
      workEmail: email,
    },
    headers: { "X-CSRF-Header": cookie, "Accept": "application/json" },
  });
};

export const getMember = async (id, cookie) => {
  return resolve({
    url: `${memberProfileUrl}/${id}`,
    headers: { "X-CSRF-Header": cookie, "Accept": "application/json" },
  });
};

export const updateMember = async (member, cookie) => {
  return resolve({
    method: "PUT",
    url: memberProfileUrl,
    data: member,
    headers: { "X-CSRF-Header": cookie, "Accept": "application/json", "Content-Type": "application/json;charset=UTF-8" },
  });
};

export const getCurrentUser = async (cookie) => {
  return resolve({
    url: `${memberProfileUrl}/current`,
    headers: { "X-CSRF-Header": cookie, "Accept": "application/json" },
  });
};

export const createMember = async (newMember, cookie) => {
  return resolve({
    method: "POST",
    url: memberProfileUrl,
    data: newMember,
    headers: { "X-CSRF-Header": cookie, "Accept": "application/json", "Content-Type": "application/json;charset=UTF-8" },
  });
};

export const deleteMember = async (id, cookie) => {
  return resolve({
    method: "DELETE",
    url: `${memberProfileUrl}/${id}`,
    headers: { "X-CSRF-Header": cookie, "Accept": "application/json" },
  });
};

export const reportAllMembersCsv = async (cookie) => {
  return resolve({
    url: memberProfileReportUrl,
    method: "POST",
    data: {},
    headers: { "X-CSRF-Header": cookie, 'Accept': 'text/csv', "Content-Type": "application/json;charset=UTF-8" },
  });
};

export const reportSelectedMembersCsv = async (memberIds, cookie) => {
  return resolve({
    url: memberProfileReportUrl,
    method: "POST",
    data: {
      memberIds: memberIds
    },
    headers: { "X-CSRF-Header": cookie, 'Accept': 'text/csv', "Content-Type": "application/json;charset=UTF-8" },
  });
};
