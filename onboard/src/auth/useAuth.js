import React, {
  useState,
  useEffect,
  useContext,
  useRef,
  createContext,
} from "react";
import { useSelector, useDispatch } from "react-redux";
import { loggedInCheck } from "./../utils/loggedInCheck";
import { isArrayPresent } from "./../utils/helperFunctions";

import fetchProfile from "./../api/fetchProfile";
import fetchToken from "./../api/fetchToken";
import fetchEducation from "../api/fetchEducation";
import fetchJobHistory from "../api/fetchJobHistory";

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
  const dispatch = useDispatch();

  const loginData = useSelector((state) => state.login);
  const profileData = useSelector((state) => state.profile);
  const educationData = useSelector((state) => state.education);
  const jobHistoryData = useSelector((state) =>state.jobhistory);
  const newData = useSelector((state) => state.data);
  const [isLoggedIn, setIsLoggedIn] = useState(true);

  let accessTokenPresent = loginData?.accessToken !== "";
  let loginCheck = loggedInCheck(loginData);

  useEffect(() => {
    if (firstLoad.current) {
      firstLoad.current = false;
      // console.log('first load... must sync token!');
      dispatch(fetchToken());
    }
  }, []);

  useEffect(() => {
    // Check for Login status upon change of LoginData
    //setIsLoggedIn(loginCheck);
    // console.log('This is loginCheck from loginData useEffect: ' + loginCheck);
    if (initialRender.current) {
      initialRender.current = false;
    }
  }, [loginData]);

  useEffect(() => {
    if (accessTokenPresent) {
      if (!isArrayPresent(profileData)) {
        // console.log("Fetch user profile");
        dispatch(fetchProfile(loginData.accessToken));
      }

      if (!isArrayPresent(educationData)) {
        console.log("Fetching new data");
        dispatch(fetchEducation(loginData.accessToken));
      } else {
        console.log("Education data already exists");
        console.log(educationData);
      }

      if (!isArrayPresent(jobHistoryData)) {
        console.log("Fetching new data");
        dispatch(fetchJobHistory(loginData.accessToken));
      } else {
        console.log("Job history data already exists");
        console.log(jobHistoryData);
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

  return {
    isLoggedIn,
  };
}
