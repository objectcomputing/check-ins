import { resolve } from "./api";

const signerUrl = "/signer";
const getSigner = async () => {
  return resolve({
    url: signerUrl,
    responseType: "json",
  });
};

export default getSigner;
