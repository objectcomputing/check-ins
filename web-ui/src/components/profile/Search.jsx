import React, { useContext, useState } from "react";
import { AppContext, MY_SKILL_ADD } from "../../context/AppContext";
import axios from "axios";
import Fuse from "fuse.js";
import { getSkills } from "../../api/skill.js";

import "./Search.css";

const Search = ({ onClick }) => {
  const { state, dispatch } = useContext(AppContext);
  const { mySkills } = state;
  const [pattern, setPattern] = useState("");
  const [skillsList, setSkillsList] = useState([]);

  // Get skills list
  React.useEffect(() => {
    async function updateSkillsList() {
      let skillsRes = await getSkills();
      setSkillsList(
        skillsRes.payload && skillsRes.payload.data
          ? skillsRes.payload.data
          : []
      );
    }
    updateSkillsList();
  }, []);

  const options = {
    includeScore: true,
    ignoreLocation: true,
    keys: ["name"],
  };

  const filter = (skillList, options) => {
    const fuse = new Fuse(skillList, options);

    return fuse.search(pattern).map((item) => {
      return item.item.name;
    });
  };

  const addSkill = (e) => {
    const value = e.target.value;
    const pending = skillsList.filter((i) => {
      return i.name.toUpperCase() === value.toUpperCase();
    });

    dispatch({
      type: MY_SKILL_ADD,
      payload: { name: value, pending: pending.length < 1 },
    });

    const inSkillsList = skillsList.find(({ name }) => {
      return name.toUpperCase() === value.toUpperCase();
    });
    if (!inSkillsList) {
      axios
        .post("/skill", { name: value, pending: "true" })
        .catch((err) => console.log(err));
    }
  };

  const filtered = filter(skillsList, options);

  return (
    <div className="search-parent">
      <div style={{ display: "inline-block" }}>
        <input
          className="search-input"
          onChange={(e) => setPattern(e.target.value)}
          onKeyPress={(e) => {
            const inMySkills = mySkills.find(({ name }) => {
              return name.toUpperCase() === pattern.toUpperCase();
            });
            if (e.key === "Enter") {
              if (inMySkills) {
                setPattern("");
                return;
              }
              setPattern("");
              addSkill(e);
            }
          }}
          placeholder="Search Skills"
          value={pattern}
        ></input>
        <div className="skills-parent">
          {filtered.map((name) => {
            return (
              <p
                className="skill"
                key={name}
                onClick={() => {
                  onClick(name);
                  setPattern("");
                }}
              >
                {name}
              </p>
            );
          })}
        </div>
      </div>
    </div>
  );
};

export default Search;
