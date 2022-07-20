import { resolve } from "./api";

const embedUrl = "/embed-signrequest";
const getEmbeddedUrl = async () => {
  return resolve({
    url: embedUrl,
    responseType: "String",
  });
};

export default getEmbeddedUrl;
