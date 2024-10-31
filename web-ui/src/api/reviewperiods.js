import { resolve } from './api.js';

const reviewPeriodsUrl = '/services/review-periods';

export const getReviewPeriods = async cookie => {
  return resolve({
    url: reviewPeriodsUrl,
    headers: { 'X-CSRF-Header': cookie, Accept: 'application/json' }
  });
};

export const getReviewPeriod = async (id, cookie) => {
  return resolve({
    url: `${reviewPeriodsUrl}/${id}`,
    headers: { 'X-CSRF-Header': cookie, Accept: 'application/json' }
  });
};

export const createReviewPeriod = async (reviewPeriod, cookie) => {
  return resolve({
    method: 'POST',
    url: reviewPeriodsUrl,
    data: reviewPeriod,
    headers: {
      'X-CSRF-Header': cookie,
      Accept: 'application/json',
      'Content-Type': 'application/json;charset=UTF-8'
    }
  });
};

export const updateReviewPeriod = async (reviewPeriod, cookie) => {
  return resolve({
    method: 'PUT',
    url: reviewPeriodsUrl,
    data: reviewPeriod,
    headers: {
      'X-CSRF-Header': cookie,
      Accept: 'application/json',
      'Content-Type': 'application/json;charset=UTF-8'
    }
  });
};

export const removeReviewPeriod = async (id, cookie) => {
  return resolve({
    method: 'DELETE',
    url: `${reviewPeriodsUrl}/${id}`,
    headers: { 'X-CSRF-Header': cookie, Accept: 'application/json' }
  });
};
