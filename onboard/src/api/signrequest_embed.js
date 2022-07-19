import { resolve } from "./api";

const embedUrl = "/embed-signrequest";
const getEmbeddedURL = async () => {
  return resolve({
    url: embedUrl,
    responseType: "String",
  });
};

export default getEmbeddedURL;
