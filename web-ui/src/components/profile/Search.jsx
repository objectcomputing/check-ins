import React, { useState } from "react";
import Fuse from "fuse.js";

import "./Profile.css";

const Search = (props) => {
  const [pattern, setPattern] = useState("");
  const { onClick } = props;

  const options = {
    includeScore: true,
    ignoreLocation: true,
    keys: ["skill"],
  };

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
          onChange={(e) => setPattern(e.target.value)}
          placeholder="Search Skills"
          value={pattern}
        ></input>
        <div>
          {filtered.map((skill) => {
            return (
              <p
                key={skill}
                onClick={() => {
                  onClick(skill);
                  setPattern("");
                }}
                style={{ border: "1px solid black", cursor: "pointer" }}
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
