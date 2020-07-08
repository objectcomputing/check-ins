import React, { useContext, useState, useMemo } from "react";
import {
  SkillsContext,
  MY_SKILL_ADD,
  MY_SKILL_REMOVE,
  MY_SKILL_TOGGLE,
} from "../../context/SkillsContext";
import Fuse from "fuse.js";

import "./Search.css";

const Search = ({ onClick }) => {
  const { state, dispatch } = useContext(SkillsContext);
  const { skillsList, mySkills } = state;
  const [pattern, setPattern] = useState("");

  const options = {
    includeScore: true,
    ignoreLocation: true,
    keys: ["skill"],
  };

  const filter = (skillList, options) => {
    const fuse = new Fuse(skillList, options);

    return fuse.search(pattern).map((item) => {
      return item.item.skill;
    });
  };

  const addSkill = (e) => {
    const value = e.target.value;
    dispatch({ type: MY_SKILL_ADD, payload: { skill: value.toUpperCase() } });
  };

  const pending = useMemo(() => {
    return (
      mySkills.filter((i) => {
        skillsList.find(({ skill }) => {
          return skill !== i.skill;
        });
      }).length > 0
    );
  }, [skillsList, mySkills]);

  const filtered = filter(skillsList, options);

  return (
    <div className="search-parent">
      <div style={{ display: "inline-block" }}>
        <input
          className="search-input"
          onChange={(e) => setPattern(e.target.value)}
          onKeyPress={(e) => {
            const inMySkills = mySkills.find(({ skill }) => {
              return skill === pattern.toUpperCase();
            });
            if (inMySkills) {
              return;
            }
            if (e.key === "Enter") {
              setPattern("");
              addSkill(e);
            }
          }}
          placeholder="Search Skills"
          value={pattern}
        ></input>
        <div className="skills-parent">
          {filtered.map((skill) => {
            return (
              <p
                className="skill"
                key={skill}
                onClick={() => {
                  onClick(skill);
                  setPattern("");
                }}
              >
                {skill}
              </p>
            );
          })}
          {/* <p>{pending ? "Pending..." : "Not Pending"}</p> */}
        </div>
      </div>
    </div>
  );
};

export default Search;
