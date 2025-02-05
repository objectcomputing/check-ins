import React, { useEffect, useReducer, useMemo } from 'react';
import { reducer, initialState } from './reducer';
import {
  SET_CSRF,
} from './actions';
import { BASE_API_URL } from '../api/api';
;

const AppContext = React.createContext();

function getSessionCookieValue(name) {
  const cookies = document?.cookie?.split(';');
  for (let i = 0; i < cookies.length; i++) {
    const cookie = cookies[i].trim();
    if (cookie.startsWith(name + '=')) {
      return decodeURIComponent(cookie.substring(name.length + 1));
    }
  }
  return null;
}

const AppContextProvider = props => {
  const [state, dispatch] = useReducer(
    reducer,
    props?.value?.state || initialState
  );

  const {
    csrf,
  } = state;
  const url = `${BASE_API_URL}/csrf/cookie`;
  useEffect(() => {
    const getCsrf = async () => {
      if (!csrf) {
        const payload = getSessionCookieValue('_csrf');
        if (payload) {
          dispatch({ type: SET_CSRF, payload });
        } else {
          const res = await fetch(url, {
            responseType: 'text',
            credentials: 'include'
          });
          if (res && res.ok) {
            dispatch({ type: SET_CSRF, payload: await res.text() });
          }
        }
      }
    };
    getCsrf();
  }, [csrf]);

  const value = useMemo(() => {
    return { state, dispatch };
  }, [state]);
  return (
    <AppContext.Provider value={props.value || value}>
      {props.children}
    </AppContext.Provider>
  );
};

export { AppContext, AppContextProvider };
