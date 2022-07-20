import { send } from "./api";

const signRequestUrl = "/embed-signrequest";
const signRequest = async () => {
  return send({
    url: signRequestUrl,
  });
};

export default signRequest;
