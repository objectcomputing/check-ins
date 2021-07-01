import { resolve } from "./api.js";

const anniversaryReportUrl = "/services/reports/anniversaries";
const birthdayReportUrl = "/services/reports/birthdays";

//
//export const getAnniversary = async (month, cookie) => {
//  return resolve({
//    url: `${anniversaryReportUrl}?month=${month}`,
//    responseType: "json",
//    headers: { "X-CSRF-Header": cookie },
//  });
//};

export const getAnniversary = async (month, cookie) => {
  return resolve({
  method: "get",
    url: `${anniversaryReportUrl}`,
    responseType: "json",
    data: month,
    headers: { "X-CSRF-Header": cookie },
  });
};

export const getBirthday = async (month, cookie) => {
  return resolve({
    url: `${birthdayReportUrl}/${month}`,
    responseType: "json",
    headers: { "X-CSRF-Header": cookie },
  });
};
//export const getBirthday = async (month, cookie) => {
//  return resolve({
//    url: `${birthdayReportUrl}/${month}`,
//    responseType: "json",
//    data: month,
//    headers: { "X-CSRF-Header": cookie },
//  });
//};