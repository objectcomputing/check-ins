import { resolve } from "./api";

const signRequestURL = "/send-request";

const getSignRequest = async () => {
  return resolve({
    url: signRequestURL,
    responseType: "json",
  });
};

export default getSignRequest;
