import axios from "axios";
import { resolve, BASE_API_URL } from "./api.js";

export const uploadFile = async (file) => {
  return await resolve(
    axios({
      method: "post",
      url: `${BASE_API_URL}/upload`,
      responseType: "json",
      body: { file: file },
    })
  );
};
