// import axios from "axios";

// class signRequest {
//   executeSendSignRequest() {
//     return axios.get("http://localhost:8080/sign-request");
//   }
// }

// export default new signRequest();

import { resolve } from "./api";

const signRequestURL = "/signer";

const getSignRequest = async () => {
  return resolve({
    url: signRequestURL,
    responseType: "json",
  });
};

export default getSignRequest;
