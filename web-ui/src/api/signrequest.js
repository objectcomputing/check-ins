import { resolve } from "./api";

const documentUrl = "/signrequest-documents";

const getDocuments = async () => {
  return resolve({
    url: documentUrl,
    responseType: "json",
  });
};

export default getDocuments;
