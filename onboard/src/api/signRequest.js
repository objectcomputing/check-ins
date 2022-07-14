import { resolve } from "./api";

const signRequestURL = "/signer";

const getSignRequest = async () => {
  return resolve({
    url: signRequestURL,
    responseType: "json",
  });
};

export default getSignRequest;
