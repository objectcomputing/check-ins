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
