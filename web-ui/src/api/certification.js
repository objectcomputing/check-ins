import { resolve } from './api.js';

const certificationUrl = '/services/certification';

export const getCertifications = async cookie => {
  return resolve({
    url: certificationUrl,
    headers: {
      'X-CSRF-Header': cookie,
      Accept: 'application/json',
      'Content-Type': 'application/json;charset=UTF-8',
    }
  });
};

export const getCertification = async (id, cookie) => {
  return resolve({
    url: `${certificationUrl}/${id}`,
    headers: { 'X-CSRF-Header': cookie, Accept: 'application/json' }
  });
};

export const createCertification = async (data, cookie) => {
  return resolve({
    method: 'POST',
    url: certificationUrl,
    headers: {
      'X-CSRF-Header': cookie,
      Accept: 'application/json',
      'Content-Type': 'application/json;charset=UTF-8'
    },
    data: data,
  });
}

export const updateCertification = async (id, data, cookie) => {
  return resolve({
    method: 'PUT',
    url: `${certificationUrl}/${id}`,
    headers: {
      'X-CSRF-Header': cookie,
      Accept: 'application/json',
      'Content-Type': 'application/json;charset=UTF-8'
    },
    data: data,
  });
}

export const mergeCertification = async (sourceId, targetId, cookie) => {
  return resolve({
    method: 'POST',
    url: `${certificationUrl}/merge`,
    headers: {
      'X-CSRF-Header': cookie,
      Accept: 'application/json',
      'Content-Type': 'application/json;charset=UTF-8'
    },
    data: { sourceId, targetId },
  });
}
