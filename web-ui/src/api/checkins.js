import { resolve } from "./api.js";

const checkinsUrl = "/services/check-in";
const checkinsNoteUrl = "/services/checkin-note";
export const getMemberCheckinsByPDL = async (memberId, pdlId) => {
  return resolve({
    method: "get",
    url: checkinsUrl,
    responseType: "json",
    params: {
      teamMemberId: memberId,
      pdlId: pdlId,
    },
  });
};

export const getCheckinByMemberId = async (id) => {
  return resolve({
    method: "get",
    url: checkinsUrl,
    responseType: "json",
    params: {
      teamMemberId: id,
    },
  });
};

export const getCheckinByPdlId = async (id) => {
  return resolve({
    method: "get",
    url: checkinsUrl,
    responseType: "json",
    params: {
      pdlId: id,
    },
  });
};

export const createCheckin = async ({
  teamMemberId,
  pdlId,
  checkInDate,
  completed,
}) => {
  return resolve({
    method: "post",
    url: checkinsUrl,
    responseType: "json",
    data: { teamMemberId, pdlId, checkInDate, completed },
  });
};

export const getNoteByCheckinId = async (id) => {
  return resolve({
    method: "get",
    url: checkinsNoteUrl,
    responseType: "json",
    params: {
      checkinid: id,
    },
  });
};

export const updateCheckin = async ({
  completed,
  id,
  teamMemberId,
  pdlId,
  checkInDate,
}) => {
  return resolve({
    method: "put",
    url: checkinsUrl,
    responseType: "json",
    data: { completed, id, teamMemberId, pdlId, checkInDate },
  });
};

export const createCheckinNote = async ({ checkinid, createdbyid, description }) => {
  return resolve({
    method: "post",
    url: checkinsNoteUrl,
    responseType: "json",
    data: { checkinid, createdbyid, description },
  });
};

export const updateCheckinNote = async ({
  id,
  checkinid,
  createdbyid,
  description,
}) => {
  return resolve({
    method: "put",
    url: checkinsNoteUrl,
    responseType: "json",
    data: { id, checkinid, createdbyid, description },
  });
};

// TODO: write get/update private note api call
