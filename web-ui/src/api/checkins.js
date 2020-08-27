import axios from "axios";
import { resolve, BASE_API_URL } from "./api.js";

const checkinsUrl = `${BASE_API_URL}/services/check-in`;
const checkinsNoteUrl = `${BASE_API_URL}/services/checkin-note`;
export const getMemberCheckinsByPDL = async (memberId, pdlId) => {
  return await resolve(
    axios({
      method: "get",
      url: checkinsUrl,
      responseType: "json",
      params: {
        teamMemberId: memberId,
        pdlId: pdlId,
      },
    })
  );
};

export const getCheckinByMemberId = async (id) => {
  return await resolve(
    axios({
      method: "get",
      url: checkinsUrl,
      responseType: "json",
      params: {
        teamMemberId: id,
      },
    })
  );
};

export const getCheckinByPdlId = async (id) => {
  return await resolve(
    axios({
      method: "get",
      url: checkinsUrl,
      responseType: "json",
      params: {
        pdlId: id,
      },
    })
  );
};

export const getNoteByCheckinId = async (id) => {
  return await resolve(
    axios({
      method: "get",
      url: checkinsNoteUrl,
      responseType: "json",
      params: {
        checkinid: id,
      },
    })
  );
};

export const updateCheckinNote = ({
  id,
  checkinid,
  createdbyid,
  description,
}) => {
  return resolve(
    axios({
      method: "put",
      url: checkinsNoteUrl,
      responseType: "json",
      data: { id, checkinid, createdbyid, description },
    })
  );
};

// TODO: write get/update private note api call
