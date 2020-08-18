import React, { useReducer, useMemo } from "react";

export const BECOME_USER = "be_user";
export const MY_PROFILE_UPDATE = "update_profile";
export const UPDATE_PDL = "update_pdl";
export const UPDATE_PDLS = "update_pdls";
export const UPDATE_CHECKIN = "update_checkin";

const AppContext = React.createContext();

let teamMembers = [];

const defaultProfile = {
  bio: "It was all a dream, I used to read Word Up magazine",
  email: "Biggie@oci.com",
  name: "Christopher Wallace",
  pdl: "Tupac Shakur",
  role: "Lyrical Poet",
};

const defaultTeamMembers = [
  {
    name: "jes",
    id: "lk134l5hg-1-1l34h-145j",
    insperityId: "example string of insperity",
    role: "engineer",
    // pdlId: "fb6424a0-b429-4edf-8f05-6927689bec5f",
    pdl: "Mark",
    location: "kihei",
    workEmail: "example email",
    startDate: 1573551461820,
    bioText: "example bio text",
  },
  {
    name: "pramukh",
    id: "lk154l5hg-5-1l34h-145j",
    role: "engineer",
    // pdl: "fb6424a0-b429-4edf-8f05-6927689bec5f",
    pdl: "Jason",
    location: "St. Louis",
    workEmail: "example email",
    insperityId: "example string of insperity",
    startDate: 1493051461820,
    bioText: "example bio text",
  },
];
defaultTeamMembers.forEach((member) => (member.selected = false));

const defaultUser = {
  uuid: "89057028-810c-4b6c-a3e8-6e6b5039722c",
  name: "string",
  role: "string",
  location: "string",
  workEmail: "string",
  insperityId: "string",
  startDate: [2020, 8, 4],
  bioText: "string",
};

const initialState = {
  defaultProfile: defaultProfile,
  defaultTeamMembers: defaultTeamMembers,
  isAdmin: true,
  teamMembers: teamMembers,
  user: defaultUser,
};

const reducer = (state, action) => {
  switch (action.type) {
    case BECOME_USER:
      state.user = action.payload;
      break;
    case MY_PROFILE_UPDATE:
      state.defaultProfile = action.payload;
      break;
    case UPDATE_PDLS: {
      const { selectedProfiles } = action.payload;
      const ids = selectedProfiles.map((p) => p.id);
      const profiles = state.defaultTeamMembers.map((tm) => {
        return ids.includes(tm.id)
          ? { ...tm, pdl: action.payload.pdl, selected: false }
          : tm;
      });
      state.defaultTeamMembers = profiles;
      break;
    }
    case UPDATE_CHECKIN:
      const { date, index } = action.payload;
      const timeString = date.getHours() + ":" + date.getMinutes();
      const day = date.getDate();
      const month = date.getMonth() + 1;
      const year = date.getFullYear();
      const today = new Date();
      const quarter = Math.floor((today.getMonth() + 3) / 3);
      const checkin = state.defaultProfile.checkins[index];
      state.defaultProfile.checkins[index] = {
        ...checkin,
        checkInDate: `${year}-${month}-${day} ${timeString}`,
        targetYear: year,
        targetQtr: quarter,
      };
      break;
    default:
  }
  return { ...state };
};

const AppContextProvider = (props) => {
  let iState = props && props.state ? props.state : initialState;
  const [state, dispatch] = useReducer(reducer, iState);
  let value = useMemo(() => {
    return { state, dispatch };
  }, [state]);
  return (
    <AppContext.Provider value={value}>{props.children}</AppContext.Provider>
  );
};

const SkillConsumer = AppContext.Consumer;

export { SkillConsumer, AppContext, AppContextProvider };
