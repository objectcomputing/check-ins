import React, { useEffect, useReducer, useMemo } from "react";
import { getMemberByEmail, updateMember } from "../api/member.js";
import { getCheckinByMemberId } from "../api/checkins";

export const MY_PROFILE_UPDATE = "update_profile";
export const UPDATE_USER_BIO = "update_bio";
export const UPDATE_USER_DATA = "update_user_data";
export const UPDATE_CHECKINS = "update_checkins";

const AppContext = React.createContext();

const reducer = (state, action) => {
  switch (action.type) {
    case MY_PROFILE_UPDATE:
      state.userProfile = action.payload;
      break;
    case UPDATE_USER_DATA:
      state.userData = action.payload;
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
  checkins: [],
  // TODO: add teamMember in view (from PDL perspective)
  userProfile: {},
  // TODO: add checkins for user
  userData: {
    email: "string",
    image_url:
      "https://upload.wikimedia.org/wikipedia/commons/thumb/7/74/SNL_MrBill_Doll.jpg/220px-SNL_MrBill_Doll.jpg",
    role: "ADMIN",
  },
};

const AppContextProvider = (props) => {
  const [state, dispatch] = useReducer(reducer, initialState);

  useEffect(() => {
    async function updateUserProfile() {
      if (state.userData.email) {
        let res = await getMemberByEmail(state.userData.email);
        let profile =
          res.payload.data && res.payload.data.length > 0 && !res.error
            ? res.payload.data[0]
            : undefined;

        if (profile) {
          dispatch({ type: MY_PROFILE_UPDATE, payload: profile });
        }
      }
    }
    updateUserProfile();
  }, [state.userData, state.userData.email]);

  useEffect(() => {
    async function getCheckins() {
      if (state.userProfile.id) {
        let res = await getCheckinByMemberId(state.userProfile.id);
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
  }, [state.userProfile.id]);

  let value = useMemo(() => {
    return { state, dispatch };
  }, [state]);
  return (
    <AppContext.Provider value={value}>{props.children}</AppContext.Provider>
  );
};

export { AppContext, AppContextProvider };
