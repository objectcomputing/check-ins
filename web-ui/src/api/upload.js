import axios from "axios";
import { resolve, BASE_API_URL } from "./api.js";

const myAxios = axios.create({ withCredentials: true });

const fileUrl = `${BASE_API_URL}/services/file`;

export const getFiles = async (checkinId) => {
  return await resolve(
    myAxios({
      method: "get",
      url: fileUrl + `?id=${checkinId}`,
      responseType: "json",
    })
  );
};

export const getAllFiles = async () => {
  return await resolve(
    myAxios({
      method: "get",
      url: fileUrl,
      responseType: "json",
    })
  );
};

export const uploadFile = async (formData, checkinId) => {
  return await resolve(
    myAxios({
      headers: { "Content-Type": "multipart/form-data" },
      method: "post",
      url: fileUrl + `/${checkinId}`,
      responseType: "json",
      data: formData,
    })
  );
};

export const deleteFile = async (fileId) => {
  console.log({ fileId });
  return await resolve(
    myAxios({
      headers: { "Content-Type": "multipart/form-data" },
      method: "delete",
      url: fileUrl + `/${fileId}`,
      responseType: "json",
    })
  );
};
