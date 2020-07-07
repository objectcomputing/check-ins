import React, { useState } from "react";
import Fuse from "fuse.js";

import "./Search.css";

const Search = ({ onClick }) => {
  const [pattern, setPattern] = useState("");

  const options = {
    includeScore: true,
    ignoreLocation: true,
    keys: ["skill"],
  };

  //list to come from db
  const list = [
    { skill: "JavaScript" },
    { skill: "Java" },
    { skill: "C++" },
    { skill: "Jquery" },
    { skill: "Node" },
    { skill: "Machine Learning" },
    { skill: "Go" },
    { skill: "Micronaut" },
  ];
  let filtered = [];

  const filter = (list, options) => {
    const fuse = new Fuse(list, options);

    let temp = fuse.search(pattern);
    temp.forEach((item) => {
      filtered.push(item.item.skill);
    });
  };

  filter(list, options);
  return (
    <div className="search-parent">
      <div style={{ display: "inline-block" }}>
        <input
          className="search-input"
          onChange={(e) => setPattern(e.target.value)}
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
        </div>
      </div>
    </div>
  );
};

export default Search;
