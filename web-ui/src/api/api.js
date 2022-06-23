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
        if (err.response.status !== 401) {
          return Promise.reject(err);
        }

        // trade in refresh token for access token
        return axios.get('/oauth/access_token', {
          baseURL: BASE_API_URL,
          withCredentials: true,
          timeout: 30000
        })
          .then(() => {
            // retry original request
            return myAxios(err.config);
          })
          .catch(() => {
            return Promise.reject(err);
          })
      }
    );
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
          toast: e?.response?.data?.message,
        },
      });
    }
  }

  return resolved;
};
