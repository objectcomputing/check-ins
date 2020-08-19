import React, { useReducer, useMemo } from "react";
import { getMemberByEmail, updateMember } from "../api/member.js";

export const MY_PROFILE_UPDATE = "update_profile";
export const UPDATE_USER_BIO = "update_bio";
export const UPDATE_USER_DATA = "update_user_data";

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
    default:
  }
  return { ...state };
};

const AppContextProvider = (props) => {
  const [state, dispatch] = useReducer(reducer, {
    userProfile: {},
    userData: {
      email: "string",
      image_url:
        "https://upload.wikimedia.org/wikipedia/commons/thumb/7/74/SNL_MrBill_Doll.jpg/220px-SNL_MrBill_Doll.jpg",
      role: "ADMIN",
    },
  });

  console.log(state.userProfile.bioText);

  React.useEffect(() => {
    async function updateUserProfile() {
      console.log("Update profile");
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

  let value = useMemo(() => {
    return { state, dispatch };
  }, [state]);
  return (
    <AppContext.Provider value={value}>{props.children}</AppContext.Provider>
  );
};

const SkillConsumer = AppContext.Consumer;

export { SkillConsumer, AppContext, AppContextProvider };
