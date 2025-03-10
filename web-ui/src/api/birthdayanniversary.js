import { resolve } from './api.js';

const anniversaryReportUrl = '/services/reports/anniversaries';
const birthdayReportUrl = '/services/reports/birthdays';
const celebrationsToday = '/services/today';

export const getAnniversaries = async (months, cookie) => {
  const results = [];
  for (let month of months) {
    const res = await resolve({
      url: `${anniversaryReportUrl}?month=${month}`,
      headers: { 'X-CSRF-Header': cookie, Accept: 'application/json' }
    });
    if (res.error) {
      console.error(res.error);
    } else {
      results.push(...res.payload.data);
    }
  }
  return results;
};

export const getTodaysCelebrations = async cookie => {
  return resolve({
    url: celebrationsToday,
    headers: { 'X-CSRF-Header': cookie, Accept: 'application/json' }
  });
};

export const getBirthdays = async (months, cookie) => {
  const results = [];
  if (months) {
    for (let month of months) {
      const res = await resolve({
        url: `${birthdayReportUrl}?month=${month}`,
        headers: { 'X-CSRF-Header': cookie, Accept: 'application/json' }
      });
      if (res.error) {
        console.error(res.error);
      } else {
        results.push(...res.payload.data);
      }
    }
  } else {
    const res = await resolve({
      url: birthdayReportUrl,
      headers: { 'X-CSRF-Header': cookie, Accept: 'application/json' }
    });
    if (res.error) {
      console.error(res.error);
    } else {
      results.push(...res.payload.data);
    }
  }
  return results;
};
