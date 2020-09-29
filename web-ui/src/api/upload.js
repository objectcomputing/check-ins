import axios from "axios";
import { resolve, BASE_API_URL } from "./api.js";

const uploadUrl = `${BASE_API_URL}/upload`;
export const uploadFile = async (file) => {
  return await resolve(
    axios({
      headers: { "Content-Type": "multipart/form-data" },
      method: "post",
      url: uploadUrl,
      responseType: "json",
      data: file,
      withCredentials: true,
    })
  );
};
