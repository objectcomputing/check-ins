import axios from "axios";
import { UPDATE_TOAST } from "../context/actions";

export const BASE_API_URL = process.env.REACT_APP_API_URL
  ? process.env.REACT_APP_API_URL
  : "http://localhost:8080";

export const getAvatarURL = (email) =>
  BASE_API_URL +
  "/services/member-profiles/member-photos/" +
  encodeURIComponent(email);

let myAxios = null;

export const getMyAxios = async () => {
  if (!myAxios) {
    myAxios = axios.create({
      baseURL: BASE_API_URL,
      withCredentials: true,
    });

    myAxios.interceptors.response.use(
      // Any status code that lie within the range of 2xx cause this function to trigger
      (res) => {
        return res;
      }, 
      // Any status codes that falls outside the range of 2xx cause this function to trigger
      (err) => {
        if (401 === err.response.status){
          // create a message for 401 error so that the Toast is updated properly
          err.response.data = {
            message: "Your session has expired.  Please refresh the page to start a new session."
          };
        }
        return Promise.reject(err);
      }
    )
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
    if (window.snackDispatch) {
      window.snackDispatch({
        type: UPDATE_TOAST,
        payload: {
          severity: "error",
          toast: e.response.data.message,
        },
      });
    }
  }

  return resolved;
};
