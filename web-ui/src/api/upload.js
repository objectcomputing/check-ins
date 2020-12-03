import { resolve } from "./api.js";

const fileUrl = "/services/file";

export const getFiles = async (checkinId, cookie) => {
  return resolve({
    method: "get",
    url: fileUrl + `?id=${checkinId}`,
    responseType: "json",
    headers: { "X-CSRF-Header": cookie },
  });
};

export const getAllFiles = async (cookie) => {
  return resolve({
    method: "get",
    url: fileUrl,
    responseType: "json",
    headers: { "X-CSRF-Header": cookie },
  });
};

export const uploadFile = async (formData, checkinId, cookie) => {
  return resolve({
    headers: { "Content-Type": "multipart/form-data", "X-CSRF-Header": cookie },
    method: "post",
    url: fileUrl + `/${checkinId}`,
    responseType: "json",
    data: formData,
  });
};

export const deleteFile = async (fileId, cookie) => {
  return resolve({
    headers: { "Content-Type": "multipart/form-data", "X-CSRF-Header": cookie },
    method: "delete",
    url: fileUrl + `/${fileId}`,
    responseType: "json",
  });
};
