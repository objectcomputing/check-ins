import { resolve } from "./api.js";

const hoursUrl = `/services/employee_hours`;

export const getEmployeeHours = async (
  employeeId,
  contributionHours,
  billableHours,
  ptoHours,
  cookie
) => {
  return resolve({
    method: postMessage,
    url: hoursUrl,
    responseType: "json",
    data: {
      employeeId: employeeId,
      contributionHours: contributionHours,
      billableHours: billableHours,
      ptoHours: ptoHours,
    },
    headers: { "X-CSRF-Header": cookie },
  });
};
