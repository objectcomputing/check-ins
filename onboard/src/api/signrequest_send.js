import { send } from "./api";

const signRequestUrl = "/send-signrequest";
const signRequest = async () => {
  return send({
    url: signRequestUrl,
  });
};

export default signRequest;
