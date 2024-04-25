import { resolve } from "./api.js";

const fileUrl = "/services/files";

export const getFiles = async (checkinId, cookie) => {
  return resolve({
    url: fileUrl + `?id=${checkinId}`,
    headers: { "X-CSRF-Header": cookie, "Accept": "application/json" },
  });
};

export const getAllFiles = async (cookie) => {
  return resolve({
    url: fileUrl,
    headers: { "X-CSRF-Header": cookie, "Accept": "application/json" },
  });
};

export const uploadFile = async (formData, checkinId, cookie) => {
  return resolve({
    headers: { "Content-Type": "multipart/form-data", "X-CSRF-Header": cookie, "Accept": "application/json" },
    method: "POST",
    url: fileUrl + `/${checkinId}`,
    data: formData,
  });
};

export const deleteFile = async (fileId, cookie) => {
  return resolve({
    headers: { "Content-Type": "multipart/form-data", "X-CSRF-Header": cookie, "Accept": "application/json" },
    method: "DELETE",
    url: fileUrl + `/${fileId}`,
  });
};
