import React, {
  useState,
  useEffect,
  useContext,
  useRef,
  createContext,
} from "react";
import { useSelector, useDispatch } from "react-redux";
import { loggedInCheck } from "../utils/loggedInCheck";
import { isArrayPresent } from "../utils/helperFunctions";
import { loggedInCheck } from "../utils/loggedInCheck";
import { isArrayPresent } from "../utils/helperFunctions";

import fetchProfile from "../api/fetchProfile";
import postLogout from "../api/postLogout";
import fetchToken from "../api/fetchToken";

const authContext = createContext();

// Provider component that wraps your app and makes auth object
// available to any child component that calls useAuth().
export function ProvideAuth({ children }) {
  const auth = useProvideAuth();
  return <authContext.Provider value={auth}>{children}</authContext.Provider>;
}

// Hook for child components to get the auth object
// and re-render when it changes.
export const useAuth = () => {
  return useContext(authContext);
};

// Provider hook that creates auth object and handles state
function useProvideAuth() {
  const initialRender = useRef(true);
  const firstLoad = useRef(true);
  const appsRender = useRef(true);
  const dispatch = useDispatch();

  const loginData = useSelector((state) => state.login);
  const profileData = useSelector((state) => state.profile);
  const newData = useSelector((state) => state.layers);
  const [isLoggedIn, setIsLoggedIn] = useState(true);
  const [isLoading, setIsLoading] = useState(false);

  let accessTokenPresent = loginData?.accessToken !== "";
  let loginCheck = loggedInCheck(loginData);

  useEffect(() => {
    if (!firstLoad.current) {
      // console.log('not first load');
    } else {
      firstLoad.current = false;
      // console.log('first load... must sync token!');
      setIsLoading(true);
      dispatch(fetchToken(setIsLoading));
    }
  }, []);

  useEffect(() => {
    // Check for Login status upon change of LoginData
    // setIsLoggedIn(loginCheck);
    // console.log('This is loginCheck from loginData useEffect: ' + loginCheck);
    if (!initialRender.current && accessTokenPresent) {
      // console.log('Access token is present but it is not the initial render');
      setIsLoading(false);
    } else {
      // console.log('First render for loginData change');
      // console.log('Whether access token is present: ' + accessTokenPresent);
      initialRender.current = false;
    }
  }, [loginData]);

  useEffect(() => {
    if (accessTokenPresent) {
      if (accountAccess) {
        if (!isArrayPresent(profileData)) {
          console.log("Fetch user profile");
          dispatch(fetchProfile(loginData.accessToken));
        }
      }

      // THIS IS WHERE ALL FIRST TIME CALLS TO LOAD DATA FROM THE BACKEND SHOULD GO!
      // if (!isArrayPresent(newData)) {
      //   console.log("Fetching new data");
      //   dispatch(fetchNewData(loginData.accessToken));
      // } else {
      //   console.log("New data already exists");
      //   console.log(newData);
      // }
    }
  }, [accessTokenPresent]);

  const signOut = () => {
    console.log("Posting logout from auth...");
    dispatch(postLogout());
  };

  return {
    isLoggedIn,
    appsLoading,
    signOut,
  };
}
