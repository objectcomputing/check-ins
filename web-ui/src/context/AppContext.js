import React, { useReducer, useMemo } from "react";
import axios from "axios";

export const MY_SKILL_ADD = "add";
export const MY_SKILL_REMOVE = "remove";
export const MY_SKILL_TOGGLE = "toggle";
export const MY_PROFILE_UPDATE = "update";
export const UPDATE_PDL = "update_pdl";
export const UPDATE_PDLS = "update_pdls";
export const UPDATE_CHECKIN = "update_checkin";

const AppContext = React.createContext();

let skillsFromDB = [];

const getSkills = async () => {
  try {
    const res = await axios({
      method: "get",
      url: "/skill/?pending=false",
      responseType: "json",
    });
    res.data.forEach((skill) => skillsFromDB.push(skill));
  } catch (error) {
    console.log(error);
  }
};

getSkills();

const skillsList = [
  { name: "JavaScript" },
  { name: "Java" },
  { name: "C++" },
  { name: "Jquery" },
  { name: "Node" },
  { name: "Machine Learning" },
  { name: "Go" },
  { name: "Micronaut" },
];

let teamMembers = [];

const getTeamMembers = async () => {
  try {
    const res = await axios({
      method: "get",
      url: "/member-profile/?pdlId=fb6424a0-b429-4edf-8f05-6927689bec5f",
      responseType: "json",
    });
    res.data.forEach((profile) => teamMembers.push(profile));
  } catch (error) {
    console.log(error);
  }
};

let checkins = [];

const getCheckIns = async () => {
  try {
    const res = await axios({
      method: "get",
      url: "/check-in/?teamMemberId=3fa85f64-5717-4562-b3fc-2c963f66afa6",
      responseType: "json",
    });
    res.data.forEach((checkin) => checkins.push(checkin));
  } catch (e) {
    console.log(e);
  }
};

getTeamMembers();
getCheckIns();

const defaultProfile = {
  bio: "It was all a dream, I used to read Word Up magazine",
  email: "Biggie@oci.com",
  name: "Christopher Wallace",
  pdl: "Tupac Shakur",
  role: "Lyrical Poet",
  nextCheckinDate: 1573551461820,
  checkins:
    checkins.length > 0
      ? checkins
      : [
          {
            id: "3fa85f64-5717-4562-b3fc-2c963f66afa6",
            teamMemberId: "3fa85g64-5717-4562-b3fc-2c963f66afa6",
            pdlId: "3fa85f64-5747-4562-b3fc-2c963f66afa6",
            checkInDate: "2020-07-31",
            targetQtr: "Q3",
            targetYear: "2019",
          },
          {
            id: "3fa85f64-5717-4562-b3fc-2c963f66afa8",
            teamMemberId: "3fa85g64-5717-4562-b3fc-2c963f66afa6",
            pdlId: "3fa85f64-5747-4562-b3fc-2c963f66afa6",
            checkInDate: "2020-08-31",
            targetQtr: "Q3",
            targetYear: "2020",
          },
        ],
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

const mySkills = [{ name: "Jquery" }, { name: "Go" }, { name: "Node" }];

const initialState = {
  defaultProfile: defaultProfile,
  defaultTeamMembers: defaultTeamMembers,
  skillsList: skillsFromDB.length > 0 ? skillsFromDB : skillsList,
  isAdmin: false,
  mySkills: mySkills,
  teamMembers: teamMembers,
};

const reducer = (state, action) => {
  switch (action.type) {
    case MY_SKILL_ADD:
      state.mySkills = state.mySkills.filter((i) => {
        return action.payload.name !== i.name;
      });
      state.mySkills.push(action.payload);
      break;
    case MY_SKILL_REMOVE:
      state.mySkills = state.mySkills.filter((i) => {
        return action.payload.name !== i.name;
      });
      break;
    case MY_SKILL_TOGGLE:
      const found = state.mySkills.find((i) => {
        return i.name === action.payload.name;
      });
      if (found) {
        state.mySkills = state.mySkills.filter((i) => {
          return i.name !== action.payload.name;
        });
      } else {
        state.mySkills.push(action.payload);
      }
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
  const [state, dispatch] = useReducer(reducer, initialState);
  let value = useMemo(() => {
    return { state, dispatch };
  }, [state]);
  return (
    <AppContext.Provider value={value}>{props.children}</AppContext.Provider>
  );
};

const SkillConsumer = AppContext.Consumer;

export { SkillConsumer, AppContext, AppContextProvider };
