import React, { useEffect, useReducer, useMemo } from "react";
import { getCurrentUser, updateMember } from "../api/member.js";
import { getCheckinByMemberId } from "../api/checkins";

export const MY_PROFILE_UPDATE = "update_profile";
export const UPDATE_USER_BIO = "update_bio";
export const UPDATE_CHECKINS = "update_checkins";

const AppContext = React.createContext();

const reducer = (state, action) => {
  switch (action.type) {
    case MY_PROFILE_UPDATE:
      state.userProfile = action.payload;
      break;
    case UPDATE_USER_BIO:
      state.userProfile.bioText = action.payload;
      updateMember(state.userProfile);
      break;
    case UPDATE_CHECKINS:
      state.checkins = action.payload;
      break;
    default:
  }
  return { ...state };
};

const initialState = {
  userProfile: undefined,
  checkins: []
};

const AppContextProvider = (props) => {
  const [state, dispatch] = useReducer(reducer, initialState);
  const memberProfile = state && state.userProfile && state.userProfile.memberProfile ? state.userProfile.memberProfile : undefined;
  const id = memberProfile ? memberProfile.uuid : undefined;


  useEffect(() => {
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

 useEffect(() => {
    async function getCheckins() {
      if (id) {
        let res = await getCheckinByMemberId(id);
        let data =
          res.payload &&
          res.payload.data &&
          res.payload.status === 200 &&
          !res.error
            ? res.payload.data
            : null;
        if (data) {
          dispatch({ type: UPDATE_CHECKINS, payload: data });
        }
      }
    }
    getCheckins();
  }, [id]);

  let value = useMemo(() => {
    return { state, dispatch };
  }, [state]);
  return (
    <AppContext.Provider value={value}>{props.children}</AppContext.Provider>
  );
};

export { AppContext, AppContextProvider };
