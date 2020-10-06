import axios from "axios";
import { resolve, BASE_API_URL } from "./api.js";

const uploadUrl = `${BASE_API_URL}/file`;

export const getFile = async ({file, checkinId}) => {
  return await resolve(
    axios({
      method: "get",
      url: uploadUrl + `?${checkinId}`,
      responseType: "json",
      data: file,
      withCredentials: true,
    })
  );
};

export const uploadFile = async ({file, checkinId}) => {
  return await resolve(
    axios({
      headers: { "Content-Type": "multipart/form-data" },
      method: "post",
      url: uploadUrl + `/${checkinId}`,
      responseType: "json",
      data: file,
      withCredentials: true,
    })
  );
};
