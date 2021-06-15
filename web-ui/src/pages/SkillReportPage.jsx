import React, { useContext, useState } from "react";

import { AppContext } from "../context/AppContext";
import { reportSkills } from "../api/memberskill.js";
import SearchResults from "../components/search-results/SearchResults";

import {
  selectOrderedSkills,
  selectCsrfToken,
  selectOrderedMemberProfiles,
} from "../context/selectors";

import { Button, TextField } from "@material-ui/core";
import Autocomplete from "@material-ui/lab/Autocomplete";

import "./SkillReportPage.css";

const SkillReportPage = (props) => {
  const { state } = useContext(AppContext);
  const csrf = selectCsrfToken(state);
  const skills = selectOrderedSkills(state);
  const memberProfiles = selectOrderedMemberProfiles(state);
  const [searchResults, setSearchResults] = useState([]);
  const [searchRequestDTO] = useState([]);
  const [searchSkills, setSearchSkills] = useState([]);
  const [editedSearchRequest, setEditedSearchRequest] = useState(
    searchRequestDTO
  );

  const handleSearch = async (searchRequestDTO) => {
    let res = await reportSkills(searchRequestDTO, csrf);
    let memberSkillsFound;
    if (res && res.payload) {
      memberSkillsFound =
        res.payload.data.teamMembers && !res.error
          ? res.payload.data.teamMembers
          : undefined;
    }
    if (memberSkillsFound && memberProfiles) {
      setSearchResults(memberSkillsFound);
    } else {
      setSearchResults(undefined);
    }
  };

  function skillsToSkillLevelDTO(skills) {
    return skills.map((skill, index) => {
      let skillLevel = {
        id: skill.id,
        level: skill.skilllevel,
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
      inclusive: inclusive,
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
          options={skills.filter(
            (skill) =>
              !searchSkills.map((sSkill) => sSkill.id).includes(skill.id)
          )}
          value={searchSkills ? searchSkills : []}
          onChange={onSkillsChange}
          getOptionLabel={(option) => option.name}
          renderInput={(params) => (
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
      <SearchResults searchResults={searchResults} />
    </div>
  );
};

export default SkillReportPage;
