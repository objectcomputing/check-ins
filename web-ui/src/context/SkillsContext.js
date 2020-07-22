import React, { useReducer, useMemo } from "react";
import axios from "axios";

export const MY_SKILL_ADD = "add";
export const MY_SKILL_REMOVE = "remove";
export const MY_SKILL_TOGGLE = "toggle";
export const MY_PROFILE_UPDATE = "update";
export const UPDATE_PDL = "update_pdl";
export const UPDATE_PDLS = "update_pdls";

const SkillsContext = React.createContext();

const skillsList = [
  { skill: "JavaScript" },
  { skill: "Java" },
  { skill: "C++" },
  { skill: "Jquery" },
  { skill: "Node" },
  { skill: "Machine Learning" },
  { skill: "Go" },
  { skill: "Micronaut" },
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

getTeamMembers();

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

const mySkills = [{ skill: "Jquery" }, { skill: "Go" }, { skill: "Node" }];

const initialState = {
  defaultProfile: defaultProfile,
  defaultTeamMembers: defaultTeamMembers,
  isAdmin: true,
  mySkills: mySkills,
  skillsList: skillsList,
  teamMembers: teamMembers,
};

const reducer = (state, action) => {
  switch (action.type) {
    case MY_SKILL_ADD:
      state.mySkills = state.mySkills.filter((i) => {
        return action.payload.skill !== i.skill;
      });
      state.mySkills.push(action.payload);
      break;
    case MY_SKILL_REMOVE:
      state.mySkills = state.mySkills.filter((i) => {
        return action.payload.skill !== i.skill;
      });
      break;
    case MY_SKILL_TOGGLE:
      const found = state.mySkills.find((i) => {
        return i === action.payload;
      });
      if (found) {
        state.mySkills = state.mySkills.filter((i) => {
          return i !== action.payload;
        });
      } else {
        state.mySkills.push(action.payload);
      }
      break;
    case MY_PROFILE_UPDATE:
      state.defaultProfile = action.payload;
      break;
    case UPDATE_PDLS: {
      const { selectedProfiles, pdl } = action.payload;
      const ids = selectedProfiles.map((p) => p.id);
      const profiles = state.defaultTeamMembers.map((tm) => {
        return ids.includes(tm.id)
          ? { ...tm, pdl: action.payload.pdl, selected: false }
          : tm;
      });
      state.defaultTeamMembers = profiles;
      break;
    }
    default:
  }
  return { ...state };
};

const SkillsContextProvider = (props) => {
  const [state, dispatch] = useReducer(reducer, initialState);
  let value = useMemo(() => {
    return { state, dispatch };
  }, [state]);
  return (
    <SkillsContext.Provider value={value}>
      {props.children}
    </SkillsContext.Provider>
  );
};

const SkillConsumer = SkillsContext.Consumer;

export { SkillConsumer, SkillsContext, SkillsContextProvider };
