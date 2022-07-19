import { resolve } from "./api";

const signRequestUrl = "/embed-signrequest";
const sendSignRequest = async () => {
  return resolve({
    url: embedUrl,
    responseType: "String",
  });
};

export default sendSignRequest;
