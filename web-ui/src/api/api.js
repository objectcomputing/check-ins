import { UPDATE_TOAST } from '../context/actions';
import qs from 'qs';

export const BASE_API_URL = import.meta.env.VITE_APP_API_URL
  ? import.meta.env.VITE_APP_API_URL
  : 'http://localhost:8080';

export const getAvatarURL = email =>
  BASE_API_URL +
  '/services/member-profiles/member-photos/' +
  encodeURIComponent(email);

function fetchAbsolute(fetch) {
  return baseUrl => (url, otherParams) =>
    url.startsWith('/')
      ? fetch(baseUrl + url, { credentials: 'include', ...otherParams })
      : fetch(url, { credentials: 'include', ...otherParams });
}

let myFetch = null;

export const getMyFetch = async () => {
  if (!myFetch) {
    myFetch = fetchAbsolute(fetch)(BASE_API_URL);

    /*
   I'm not sure this was working before, but we need to figure out an approach for fetch. I will
   open an issue for this.

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
  */
  }
  return myFetch;
};

export const resolve = async payload => {
  let { url } = payload;
  const { params = null, data = null, ...rest } = payload;
  const myFetch = await getMyFetch();

  // Convert params to fetch style...
  params && (url = `${url}?` + qs.stringify(params, { arrayFormat: 'repeat' }));

  // Convert data to body...
  data && (rest.body = JSON.stringify(data));

  const promise = myFetch(url, rest);
  const resolved = {
    payload: null,
    error: null
  };

  resolved.payload = await promise;
  if (!resolved.payload.ok) {
    const statusText = resolved.payload.statusText;
    resolved.error = await resolved.payload.json();
    if (window.snackDispatch) {
      window.snackDispatch({
        type: UPDATE_TOAST,
        payload: {
          severity: 'error',
          toast: resolved?.error?.message || statusText
        }
      });
    }
  } else {
    const contentType = resolved.payload.headers.get('Content-Type');
    const contentLength = resolved.payload.headers.get('Content-Length');
    if (contentType && contentType.indexOf('text/csv') !== -1) {
      resolved.payload.data = await resolved.payload.blob();
    } else if (contentLength && contentLength > 0) {
      if (contentType && contentType.indexOf('application/json') !== -1) {
        resolved.payload.data = await resolved.payload.json();
      } else {
        resolved.payload.data = await resolved.payload.text();
      }
    } else {
      resolved.payload.data = null;
    }
  }
  return resolved;
};
