import { resolve } from "./api.js";

const checkinsUrl = "/services/check-in";
const checkinsNoteUrl = "/services/checkin-note";
export const getMemberCheckinsByPDL = async (memberId, pdlId, cookie) => {
  return resolve({
    url: checkinsUrl,
    responseType: "json",
    params: {
      teamMemberId: memberId,
      pdlId: pdlId,
    },
    headers: { "X-CSRF-Header": cookie },
  });
};

export const getCheckinByMemberId = async (id, cookie) => {
  return resolve({
    url: checkinsUrl,
    responseType: "json",
    params: {
      teamMemberId: id,
    },
    headers: { "X-CSRF-Header": cookie },
  });
};

export const getCheckinByPdlId = async (id, cookie) => {
  return resolve({
    url: checkinsUrl,
    responseType: "json",
    params: {
      pdlId: id,
    },
    headers: { "X-CSRF-Header": cookie },
  });
};

export const createCheckin = async ({
  teamMemberId,
  pdlId,
  checkInDate,
  completed,
  cookie,
}) => {
  return resolve({
    method: "post",
    url: checkinsUrl,
    responseType: "json",
    data: { teamMemberId, pdlId, checkInDate, completed },
    headers: { "X-CSRF-Header": cookie },
  });
};

export const getNoteByCheckinId = async (id, cookie) => {
  return resolve({
    url: checkinsNoteUrl,
    responseType: "json",
    params: {
      checkinid: id,
    },
    headers: { "X-CSRF-Header": cookie },
  });
};

export const updateCheckin = async ({
  completed,
  id,
  teamMemberId,
  pdlId,
  checkInDate,
  cookie,
}) => {
  return resolve({
    method: "put",
    url: checkinsUrl,
    responseType: "json",
    data: { completed, id, teamMemberId, pdlId, checkInDate },
    headers: { "X-CSRF-Header": cookie },
  });
};

export const createCheckinNote = async ({
  checkinid,
  createdbyid,
  description,
  cookie,
}) => {
  return resolve({
    method: "post",
    url: checkinsNoteUrl,
    responseType: "json",
    data: { checkinid, createdbyid, description },
    headers: { "X-CSRF-Header": cookie },
  });
};

export const updateCheckinNote = async ({
  id,
  checkinid,
  createdbyid,
  description,
  cookie,
}) => {
  return resolve({
    method: "put",
    url: checkinsNoteUrl,
    responseType: "json",
    data: { id, checkinid, createdbyid, description },
    headers: { "X-CSRF-Header": cookie },
  });
};

// TODO: write get/update private note api call
