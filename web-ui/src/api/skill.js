import axios from "axios";
import { resolve, BASE_API_URL } from "./api.js";

export const getSkills = async () => {
  return await resolve(
    axios({
      method: "get",
      url: `${BASE_API_URL}/services/skill/?pending=false`,
      responseType: "json",
    })
  );
};
