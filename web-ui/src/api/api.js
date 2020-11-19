import axios from "axios";
import { UPDATE_TOAST } from "../context/AppContext";

export const BASE_API_URL = process.env.REACT_APP_API_URL
  ? process.env.REACT_APP_API_URL
  : "http://localhost:8080";

let myAxios = null;

export const getMyAxios = async () => {
  if (!myAxios) {
    myAxios = axios.create({
      baseURL: BASE_API_URL,
      withCredentials: true,
    });
  }
  return myAxios;
};

export const resolve = async (payload) => {
  const myAxios = await getMyAxios();
  const promise = myAxios(payload);
  const resolved = {
    payload: null,
    error: null,
  };

  try {
    resolved.payload = await promise;
  } catch (e) {
    resolved.error = e;
    console.log(e);
    if (window.snackDispatch) {
      window.snackDispatch({
        type: UPDATE_TOAST,
        payload: {
          severity: "error",
          toast: e.message,
        },
      });
    }
  }

  return resolved;
};
