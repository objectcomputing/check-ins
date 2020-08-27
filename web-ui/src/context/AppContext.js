import React, { useReducer, useMemo } from "react";
import { getCurrentUser, updateMember } from "../api/member.js";

export const MY_PROFILE_UPDATE = "update_profile";
export const UPDATE_USER_BIO = "update_bio";

const AppContext = React.createContext();

const reducer = (state, action) => {
  switch (action.type) {
    case MY_PROFILE_UPDATE:
      state.userProfile = action.payload;
      break;
    case UPDATE_USER_BIO:
      state.userProfile.memberProfile.bioText = action.payload;
      updateMember(state.userProfile.memberProfile);
      break;
    default:
  }
  return { ...state };
};

const initialState = {
  userProfile: undefined,
};

const AppContextProvider = (props) => {
  const [state, dispatch] = useReducer(reducer, initialState);

  React.useEffect(() => {
    async function updateUserProfile() {
      let res = await getCurrentUser();
      let profile =
        res.payload && res.payload.data && !res.error
          ? res.payload.data
          : undefined;

      if (profile) {
        dispatch({ type: MY_PROFILE_UPDATE, payload: profile });
      }
    }
    updateUserProfile();
  }, []);

  let value = useMemo(() => {
    return { state, dispatch };
  }, [state]);
  return (
    <AppContext.Provider value={value}>{props.children}</AppContext.Provider>
  );
};

export { AppContext, AppContextProvider };
