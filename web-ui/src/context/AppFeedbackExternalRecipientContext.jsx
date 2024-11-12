import React, { useEffect, useReducer, useMemo } from 'react';
import {reducerFeedbackExternalRecipient, initialState} from "./reducerFeedbackExternalRecilpients.js";
import {BASE_API_URL} from "../api/apiFeedbackExternalRecipient.js";
import {SET_CSRF} from "./actionsFeedbackExternalRecipient.js";

const AppFeedbackExternalRecipientContext = React.createContext();

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

const AppFeedbackExternalRecipientContextProvider = props => {
  console.log("AppFeedbackExternalRecipientContextProvider props: ", props);
  const [state, dispatch] = useReducer(
      reducerFeedbackExternalRecipient,
    props?.value?.state || initialState
  );

  const {
    csrf,
  } = state;
  const url = `${BASE_API_URL}/csrf/cookie`;

  useEffect(() => {

    console.log("AppFeedbackExternalRecipientContextProvider useEffect, csrf");

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
    console.log("AppFeedbackExternalRecipientContextProvider useMemo, state: ", state);
    console.log("AppFeedbackExternalRecipientContextProvider useMemo, dispatch: ", dispatch);
    return { state, dispatch };
  }, [state]);

  return (
    <AppFeedbackExternalRecipientContext.Provider value={props.value || value}>
      {props.children}
    </AppFeedbackExternalRecipientContext.Provider>
  );
};

export { AppFeedbackExternalRecipientContext, AppFeedbackExternalRecipientContextProvider };
