import { resolve } from "./api";

const documentUrl = "/services/documents";
const roleDocumentsUrl = "/services/documents/role-documents";

export const createDocument = async (document, cookie) => {
  return resolve({
    method: "post",
    url: documentUrl,
    responseType: "json",
    data: document,
    headers: { "X-CSRF-Header": cookie }
  });
};

export const getDocumentsByRole = async (roleId, cookie) => {
  return resolve({
    url: `${roleDocumentsUrl}/${roleId}`,
    responseType: "json",
    headers: { "X-CSRF-Header": cookie }
  });
};

export const getAllDocuments = async (cookie) => {
  return resolve({
    url: roleDocumentsUrl,
    responseType: "json",
    headers: { "X-CSRF-Header": cookie }
  });
}

export const updateDocument = async (document, cookie) => {
  return resolve({
    method: "put",
    url: documentUrl,
    responseType: "json",
    data: document,
    headers: { "X-CSRF-Header": cookie }
  });
};

export const addRoleAccessToDocument = async (roleId, documentId, cookie) => {
  return resolve({
    method: "post",
    url: `${roleDocumentsUrl}/${roleId}/${documentId}`,
    data: {
      roleId: roleId,
      documentId: documentId
    },
    responseType: "json",
    headers: { "X-CSRF-Header": cookie }
  });
};

export const removeRoleAccessToDocument = async (roleId, documentId, cookie) => {
  return resolve({
    method: "delete",
    url: `${roleDocumentsUrl}/${roleId}/${documentId}`,
    responseType: "json",
    headers: { "X-CSRF-Header": cookie }
  });
};
