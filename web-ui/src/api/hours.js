import { resolve } from "./api.js";

const hoursUrl = `/services/employee/hours`;

export const getEmployeeHours = async (cookie, employeeId) => {
  return resolve({
    url: hoursUrl,
    responseType: "json",
    params: {
      employeeId,
    },
    headers: { "X-CSRF-Header": cookie },
  });
};

export const postEmployeeHours = async (cookie, file) => {
  return resolve({
    method: "post",
    url: hoursUrl + "/upload",
    responseType: "json",
    data: file,
    headers: { "Content-Type": "multipart/form-data", "X-CSRF-Header": cookie },
  });
};
