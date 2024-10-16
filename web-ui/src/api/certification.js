import { resolve } from './api.js';

const certificationUrl = '/services/certification';

export const getCertifications = async cookie => {
  return resolve({
    url: certificationUrl,
    headers: { 'X-CSRF-Header': cookie, Accept: 'application/json' }
  });
};

export const getCertification = async (id, cookie) => {
  return resolve({
    url: `${certificationUrl}/${id}`,
    headers: { 'X-CSRF-Header': cookie, Accept: 'application/json' }
  });
};
