import { resolve } from "./api.js";

const checkinsUrl = "/services/check-ins";
const checkinsNoteUrl = "/services/checkin-notes";
const checkinsPrivateNoteUrl = "/services/private-notes";

export const getMemberCheckinsByPDL = async (memberId, pdlId, cookie) => {
  return resolve({
    url: checkinsUrl,
    params: {
      teamMemberId: memberId,
      pdlId: pdlId,
    },
    headers: { "X-CSRF-Header": cookie, "Accept": "application/json" },
  });
};

export const getCheckinByMemberId = async (id, cookie) => {
  return resolve({
    url: checkinsUrl,
    params: {
      teamMemberId: id,
    },
    headers: { "X-CSRF-Header": cookie, "Accept": "application/json" },
  });
};

export const getAllCheckins = async (cookie) => {
  return resolve({
    url: checkinsUrl,
    headers: { "X-CSRF-Header": cookie, "Accept": "application/json" },
  });
};

export const getCheckinByPdlId = async (id, cookie) => {
  return resolve({
    url: checkinsUrl,
    params: {
      pdlId: id,
    },
    headers: { "X-CSRF-Header": cookie, "Accept": "application/json" },
  });
};

export const createCheckin = async (
  { teamMemberId, pdlId, checkInDate, completed },
  cookie
) => {
  return resolve({
    method: "POST",
    url: checkinsUrl,
    data: { teamMemberId, pdlId, checkInDate, completed },
    headers: { "X-CSRF-Header": cookie, "Accept": "application/json", "Content-Type": "application/json;charset=UTF-8" },
  });
};

export const getNoteByCheckinId = async (id, cookie) => {
  return resolve({
    url: checkinsNoteUrl,
    params: {
      checkinid: id,
    },
    headers: { "X-CSRF-Header": cookie, "Accept": "application/json" },
  });
};

export const updateCheckin = async (
  { completed, id, teamMemberId, pdlId, checkInDate },
  cookie
) => {
  return resolve({
    method: "PUT",
    url: checkinsUrl,
    data: { completed, id, teamMemberId, pdlId, checkInDate },
    headers: { "X-CSRF-Header": cookie, "Accept": "application/json", "Content-Type": "application/json;charset=UTF-8" },
  });
};

export const createCheckinNote = async (
  { checkinid, createdbyid, description },
  cookie
) => {
  return resolve({
    method: "POST",
    url: checkinsNoteUrl,
    data: { checkinid, createdbyid, description },
    headers: { "X-CSRF-Header": cookie, "Accept": "application/json", "Content-Type": "application/json;charset=UTF-8" },
  });
};

export const updateCheckinNote = async (
  { id, checkinid, createdbyid, description },
  cookie
) => {
  return resolve({
    method: "PUT",
    url: checkinsNoteUrl,
    data: { id, checkinid, createdbyid, description },
    headers: { "X-CSRF-Header": cookie, "Accept": "application/json", "Content-Type": "application/json;charset=UTF-8" },
  });
};

export const getPrivateNoteByCheckinId = async (id, cookie) => {
  return resolve({
    url: checkinsPrivateNoteUrl,
    params: {
      checkinid: id,
    },
    headers: { "X-CSRF-Header": cookie, "Accept": "application/json" },
  });
};

export const createPrivateNote = async (
  { checkinid, createdbyid, description },
  cookie
) => {
  return resolve({
    method: "POST",
    url: checkinsPrivateNoteUrl,
    data: { checkinid, createdbyid, description },
    headers: { "X-CSRF-Header": cookie, "Accept": "application/json", "Content-Type": "application/json;charset=UTF-8" },
  });
};

export const updatePrivateNote = async (
  { id, checkinid, createdbyid, description },
  cookie
) => {
  return resolve({
    method: "PUT",
    url: checkinsPrivateNoteUrl,
    data: { id, checkinid, createdbyid, description },
    headers: { "X-CSRF-Header": cookie, "Accept": "application/json", "Content-Type": "application/json;charset=UTF-8" },
  });
};
