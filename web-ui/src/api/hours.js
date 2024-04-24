import { resolve } from './api.js';

const hoursUrl = `/services/employee/hours`;

export const getEmployeeHours = async (cookie, employeeId) => {
  return resolve({
    url: hoursUrl,
    params: {
      employeeId
    },
    headers: { 'X-CSRF-Header': cookie, Accept: 'application/json' }
  });
};

export const postEmployeeHours = async (cookie, file) => {
  return resolve({
    method: 'POST',
    url: hoursUrl + '/upload',
    data: file,
    headers: {
      'X-CSRF-Header': cookie,
      Accept: 'application/json',
      'Content-Type': 'multipart/form-data'
    }
  });
};
