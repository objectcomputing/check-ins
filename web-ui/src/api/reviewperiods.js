import { resolve } from "./api.js";

const reviewPeriodsUrl = "/services/review-periods";

export const getReviewPeriods = async (cookie) => {
  return resolve({
    url: reviewPeriodsUrl,
    responseType: "json",
    headers: { "X-CSRF-Header": cookie },
  });
};

export const getReviewPeriod = async (id, cookie) => {
  return resolve({
    url: `${reviewPeriodsUrl}/${id}`,
    responseType: "json",
    headers: { "X-CSRF-Header": cookie },
  });
};

export const createReviewPeriod = async (reviewPeriod, cookie) => {
  return resolve({
    method: "post",
    url: reviewPeriodsUrl,
    responseType: "json",
    data: reviewPeriod,
    headers: { "X-CSRF-Header": cookie },
  });
};

export const updateReviewPeriod = async (reviewPeriod, cookie) => {
  return resolve({
    method: "put",
    url: reviewPeriodsUrl,
    responseType: "json",
    data: reviewPeriod,
    headers: { "X-CSRF-Header": cookie },
  });
};

export const removeReviewPeriod = async (id, cookie) => {
  return resolve({
    method: "delete",
    url: `${reviewPeriodsUrl}/${id}`,
    responseType: "json",
    headers: { "X-CSRF-Header": cookie },
  });
};
