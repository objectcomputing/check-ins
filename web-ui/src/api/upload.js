import { resolve } from "./api.js";

const fileUrl = "/services/file";

export const getFiles = async (checkinId) => {
  return resolve({
    method: "get",
    url: fileUrl + `?id=${checkinId}`,
    responseType: "json",
  });
};

export const getAllFiles = async () => {
  return resolve({
    method: "get",
    url: fileUrl,
    responseType: "json",
  });
};

export const uploadFile = async (formData, checkinId) => {
  return resolve({
    headers: { "Content-Type": "multipart/form-data" },
    method: "post",
    url: fileUrl + `/${checkinId}`,
    responseType: "json",
    data: formData,
  });
};

export const deleteFile = async (fileId) => {
  return resolve({
    headers: { "Content-Type": "multipart/form-data" },
    method: "delete",
    url: fileUrl + `/${fileId}`,
    responseType: "json",
  });
};
