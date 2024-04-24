import React, { useContext, useState } from 'react';

import { AppContext } from "../context/AppContext";
import { reportSkills } from "../api/memberskill.js";
import SearchResults from "../components/search-results/SearchResults";
import { sortMembersBySkill } from "../helpers/checks.js";

import {
  selectOrderedSkills,
  selectCsrfToken,
  selectCurrentMemberIds
} from '../context/selectors';

import { Button, TextField } from "@mui/material";
import Autocomplete from "@mui/material/Autocomplete";

import './SkillReportPage.css';

const SkillReportPage = props => {
  const { state } = useContext(AppContext);
  const csrf = selectCsrfToken(state);
  const skills = selectOrderedSkills(state);
  const memberIds = selectCurrentMemberIds(state);
  const [searchResults, setSearchResults] = useState([]);
  const [searchRequestDTO] = useState([]);
  const [searchSkills, setSearchSkills] = useState([]);
  const [editedSearchRequest, setEditedSearchRequest] =
    useState(searchRequestDTO);

  const handleSearch = async searchRequestDTO => {
    let res = await reportSkills(searchRequestDTO, csrf);
    let memberSkillsFound;
    if (res && res.payload) {
      memberSkillsFound =
        res.payload.data.teamMembers && !res.error
          ? res.payload.data.teamMembers
          : undefined;
    }
    // Filter out skills of terminated members
    memberSkillsFound = memberSkillsFound.filter((memberSkill) =>
      memberIds.includes(memberSkill.id)
    );
    if (memberSkillsFound && memberIds) {
      let newSort = sortMembersBySkill(memberSkillsFound);
      setSearchResults(newSort);
    } else {
      setSearchResults([]);
    }
  };

  function skillsToSkillLevelDTO(skills) {
    return skills.map((skill, index) => {
      let skillLevel = {
        id: skill.id,
        level: skill.skilllevel
      };
      return skillLevel;
    });
  }

  function createRequestDTO(editedSearchRequest) {
    let skills = skillsToSkillLevelDTO(searchSkills);
    let members = [];
    let inclusive = false;
    let newSearchRequest = {
      skills: skills,
      members: members,
      inclusive: inclusive
    };
    setEditedSearchRequest(newSearchRequest);
    return newSearchRequest;
  }

  function onSkillsChange(event, newValue) {
    let skillsCopy = newValue.sort((a, b) => a.name.localeCompare(b.name));
    setSearchSkills([...skillsCopy]);
  }

  return (
    <div className="skills-report-page">
      <div className="SkillReportModal">
        <h2>Select desired skills...</h2>
        <Autocomplete
          id="skillSelect"
          multiple
          options={skills}
          filterSelectedOptions
          value={searchSkills ? searchSkills : []}
          onChange={onSkillsChange}
          getOptionLabel={option => option.name}
          renderInput={params => (
            <TextField
              {...params}
              className="fullWidth"
              label="Skills *"
              placeholder="Add a skill..."
            />
          )}
        />
        <div className="SkillsSearch-actions fullWidth">
          <Button
            onClick={() => {
              handleSearch(createRequestDTO(editedSearchRequest));
            }}
            color="primary"
          >
            Run Search
          </Button>
        </div>
      </div>
      <SearchResults
        searchResults={searchResults}
      />
    </div>
  );
};

export default SkillReportPage;
