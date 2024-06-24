import {resolve} from './api.js';

const documentUrl = `/services/document`;

export const getDocumentsForRoleId = async (roleId, cookie) => {
  return resolve({
    url: `${documentUrl}/${roleId}`,
    headers: { 'X-CSRF-Header': cookie, Accept: 'application/json' }
  });
};
