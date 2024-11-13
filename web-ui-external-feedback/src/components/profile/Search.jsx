import React, { useContext, useState } from 'react';
import { AppContext } from '../../context/AppContext';

import Fuse from 'fuse.js';

import './Search.css';

const Search = ({ mySkills, addSkill }) => {
  const { state } = useContext(AppContext);
  const { skills } = state;
  const skillsList = skills.filter(({ pending }) => !pending);
  const [pattern, setPattern] = useState('');

  const options = {
    includeScore: true,
    ignoreLocation: true,
    keys: ['name']
  };

  const filter = (skillList, options) => {
    const fuse = new Fuse(skillList, options);

    return fuse.search(pattern).map(item => {
      return item.item.name;
    });
  };

  const filtered = filter(skillsList, options);

  return (
    <div className="search-parent">
      <div style={{ display: 'inline-block' }}>
        <input
          className="search-input"
          onChange={e => setPattern(e.target.value)}
          onKeyPress={e => {
            const inMySkills =
              mySkills.length > 0
                ? mySkills.find(
                    skill => skill.name.toUpperCase() === pattern.toUpperCase()
                  )
                : undefined;
            if (e.key === 'Enter') {
              if (inMySkills) {
                setPattern('');
              } else {
                setPattern('');
                addSkill(e.target.value);
              }
            }
          }}
          placeholder="Search Skills"
          value={pattern}
        ></input>
        <div className="skills-parent">
          {filtered.map(name => {
            return (
              <p
                className="skill"
                key={name}
                onClick={() => {
                  addSkill(name);
                  setPattern('');
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
