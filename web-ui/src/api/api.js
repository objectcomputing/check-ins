import axios from "axios";
import { UPDATE_TOAST } from "../context/AppContext";

export const BASE_API_URL = process.env.REACT_APP_API_URL
  ? process.env.REACT_APP_API_URL
  : "http://localhost:8080";

const getCsrf = async () => {
  let csrf = sessionStorage.getItem("csrf");
  if (!csrf) {
    const res = await axios({
      url: "http://localhost:8080/csrf/cookie",
      responseType: "text",
      withCredentials: true,
    });
    if (res && res.data) {
      csrf = res.data._csrf;
      sessionStorage.setItem("csrf", csrf);
    }
  }
  return csrf;
};

let myAxios = null;
let headers = null;

export const getMyAxios = async () => {
  const csrf = await getCsrf();
  if (!headers) {
    headers = { "X-CSRF-Header": csrf };
  }
  if (!myAxios) {
    myAxios = axios.create({
      baseURL: BASE_API_URL,
      headers,
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
    window.snackDispatch({
      type: UPDATE_TOAST,
      payload: {
        severity: "error",
        toast: e.message,
      },
    });
  }

  return resolved;
};
