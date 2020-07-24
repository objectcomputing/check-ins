import React, { useReducer, useMemo } from "react";
import axios from "axios";

export const MY_SKILL_ADD = "add";
export const MY_SKILL_REMOVE = "remove";
export const MY_SKILL_TOGGLE = "toggle";
export const MY_PROFILE_UPDATE = "update";

const SkillsContext = React.createContext();

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
    role: "engineer",
    pdlId: "fb6424a0-b429-4edf-8f05-6927689bec5f",
    location: "kihei",
    workEmail: "example email",
    startDate: 1573551461820,
    bioText: "example bio text",
  },
  {
    name: "pramukh",
    role: "engineer",
    pdlId: "fb6424a0-b429-4edf-8f05-6927689bec5f",
    location: "St. Louis",
    workEmail: "example email",
    insperityId: "example string of insperity",
    startDate: 1493051461820,
    bioText: "example bio text",
  },
];

const mySkills = [{ name: "Jquery" }, { name: "Go" }, { name: "Node" }];

const initialState = {
  defaultProfile: defaultProfile,
  defaultTeamMembers: defaultTeamMembers,
  skillsList: skillsFromDB.length > 0 ? skillsFromDB : skillsList,
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
