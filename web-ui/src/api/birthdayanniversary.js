import { resolve } from "./api.js";

const anniversaryReportUrl = "/services/reports/anniversaries";
const birthdayReportUrl = "/services/reports/birthdays";
const celebrationsToday = "/services/today";

export const getAnniversary = async (month, cookie) => {
  return resolve({
    url: `${anniversaryReportUrl}?month=${month}`,
    responseType: "json",
    headers: { "X-CSRF-Header": cookie },
  });
};

export const getTodaysCelebrations = async (cookie) => {
  return resolve({
    url: celebrationsToday,
    responseType: "json",
    headers: { "X-CSRF-Header": cookie },
  });
}; 

export const getBirthday = async (month, cookie) => {
  return resolve({
    url: `${birthdayReportUrl}?month=${month}`,
    responseType: "json",
    headers: { "X-CSRF-Header": cookie },
  });
};