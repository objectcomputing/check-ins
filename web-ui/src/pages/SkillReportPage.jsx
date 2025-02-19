import React, { useContext, useRef, useState, useEffect } from 'react';

import { Button, TextField } from '@mui/material';
import Autocomplete from '@mui/material/Autocomplete';

import { UPDATE_TOAST } from '../context/actions';
import { AppContext } from '../context/AppContext';
import { reportSkills } from '../api/memberskill.js';
import SearchResults from '../components/search-results/SearchResults';
import { sortMembersBySkill } from '../helpers/checks.js';

import {
  selectOrderedSkills,
  selectCsrfToken,
  selectCurrentMemberIds,
  selectHasSkillsReportPermission,
  noPermission,
} from '../context/selectors';

import { useQueryParameters } from '../helpers/query-parameters';

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

  const processedQPs = useRef(false);
  useQueryParameters(
    [
      {
        name: 'skills',
        default: [],
        value: searchSkills,
        setter(ids) {
          const searchSkills = ids.map(id =>
            skills.find(skill => skill.id === id)
          );
          setSearchSkills(searchSkills);
        },
        toQP() {
          return searchSkills.map(skill => skill.id).join(',');
        }
      }
    ],
    [skills],
    processedQPs
  );

  useEffect(() => {
    const handleSearch = async () => {
      let memberSkillsFound = [];

      if (searchSkills.length > 0) {
        const searchRequestDTO = createRequestDTO(editedSearchRequest);
        const res = await reportSkills(searchRequestDTO, csrf);
        if (res && res.payload) {
          memberSkillsFound =
            !res.error && res.payload.data?.teamMembers
              ? res.payload.data.teamMembers
              : [];
        }
        memberSkillsFound = sortMembersBySkill(memberSkillsFound);
      }

      setSearchResults(memberSkillsFound);
    };

    handleSearch();
  }, [searchSkills]);


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

  return selectHasSkillsReportPermission(state) ? (
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
      </div>
      <SearchResults searchResults={searchResults} />
    </div>
  ) : (
    <h3>{noPermission}</h3>
  );
};

export default SkillReportPage;
