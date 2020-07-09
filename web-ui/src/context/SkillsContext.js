import React, { useReducer, useMemo } from "react";

export const MY_SKILL_ADD = "add";
export const MY_SKILL_REMOVE = "remove";
export const MY_SKILL_TOGGLE = "toggle";

const SkillsContext = React.createContext();

const skillsList = [
  { skill: "JavaScript" },
  { skill: "Java" },
  { skill: "C++" },
  { skill: "Jquery" },
  { skill: "Node" },
  { skill: "Machine Learning" },
  { skill: "Go" },
  { skill: "MIicronaut" },
];

const mySkills = [{ skill: "Jquery" }, { skill: "Go" }, { skill: "Node" }];

const initialState = {
  skillsList: skillsList,
  mySkills: mySkills,
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

    default:
  }
  return { ...state };
};

const SkillsContextProvider = (props) => {
  const [state, dispatch] = useReducer(reducer, initialState);
  let value = useMemo(() => {
    return { state, dispatch };
  });
  return (
    <SkillsContext.Provider value={value}>
      {props.children}
    </SkillsContext.Provider>
  );
};

const SkillConsumer = SkillsContext.Consumer;

export { SkillConsumer, SkillsContext, SkillsContextProvider };
