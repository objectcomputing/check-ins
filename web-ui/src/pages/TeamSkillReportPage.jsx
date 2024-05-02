import React, { useContext, useRef, useState } from 'react';

import { Autocomplete, Button, TextField, Typography } from '@mui/material';

import { reportSkills } from '../api/memberskill.js';
import SearchResults from '../components/search-results/SearchResults';
import { UPDATE_TOAST } from '../context/actions';
import { AppContext } from '../context/AppContext';
import {
  selectCsrfToken,
  selectOrderedMemberFirstName,
  selectOrderedSkills,
  selectSkill
} from '../context/selectors';
import { levelMap } from '../context/util';
import { sortMembersBySkill } from '../helpers/checks.js';

import './TeamSkillReportPage.css';
import MemberSelector from '../components/member_selector/MemberSelector';
import MemberSkillRadar from '../components/member_skill_radar/MemberSkillRadar.jsx';
import { useQueryParameters } from '../helpers/query-parameters';

const TeamSkillReportPage = () => {
  const { state } = useContext(AppContext);

  const csrf = selectCsrfToken(state);
  const skills = selectOrderedSkills(state);
  const memberProfiles = selectOrderedMemberFirstName(state);

  const [selectedMembers, setSelectedMembers] = useState([]);
  const [searchResults, setSearchResults] = useState([]);
  const [allSearchResults, setAllSearchResults] = useState([]);
  const [searchSkills, setSearchSkills] = useState([]);
  const [editedSearchRequest, setEditedSearchRequest] = useState([]);
  const [showRadar, setShowRadar] = useState(false);

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

  const handleSearch = async searchRequestDTO => {
    let res = await reportSkills(searchRequestDTO, csrf);
    let memberSkillsFound;
    if (res && res.payload) {
      memberSkillsFound =
        res.payload.data.teamMembers && !res.error
          ? res.payload.data.teamMembers
          : undefined;
    }
    if (memberSkillsFound && memberProfiles) {
      setAllSearchResults(memberSkillsFound);
      let membersSelected = memberSkillsFound.filter(mSkill =>
        selectedMembers.some(member => member.id === mSkill.id)
      );
      let newSort = sortMembersBySkill(membersSelected);
      setSearchResults(newSort);
    } else {
      setSearchResults([]);
      setAllSearchResults([]);
    }
    setShowRadar(true);
  };

  function skillsToSkillLevel(skills) {
    return skills.map(skill => {
      return {
        id: skill.id,
        level: skill.skilllevel
      };
    });
  }

  function createRequest(editedSearchRequest) {
    let newSearchRequest = {
      skills: skillsToSkillLevel(searchSkills),
      members: []
    };
    setEditedSearchRequest(newSearchRequest);
    return newSearchRequest;
  }

  function onSkillsChange(event, newValue) {
    let skillsCopy = newValue.sort((a, b) => a.name.localeCompare(b.name));
    setSearchSkills([...skillsCopy]);
  }

  const skillMap = {};

  const selectedMembersCopy = selectedMembers.map(member => ({ ...member }));
  let searchResultsCopy = searchResults.map(result => ({ ...result }));
  const filteredResults = searchResultsCopy.filter(result => {
    return selectedMembersCopy.some(member => {
      return result.name === member.name;
    });
  });

  for (const result of filteredResults) {
    const memberName = result.name;

    for (const skill of result.skills) {
      const { id } = skill;

      const skillObj = selectSkill(state, skill.id);

      if (skillObj) {
        const skillName = skillObj.name;
        let value = skillMap[id];
        if (!value) {
          value = { skill: skillName };
          skillMap[id] = value;
        }
        value[memberName] = levelMap[skill.level];
      } else {
        console.error(`No skill with id ${id} found!`);
      }
    }
  }

  const chartData = Object.values(skillMap);

  for (const member of selectedMembersCopy) {
    for (const data of chartData) {
      if (!data[member.name]) {
        data[member.name] = 0;
      }
    }
  }

  return (
    <div className="team-skill-report-page">
      <MemberSelector
        className="team-skill-member-selector"
        listHeight={300}
        onChange={selected => setSelectedMembers(selected)}
      />
      <div className="select-skills-section">
        <Autocomplete
          id="skillSelect"
          multiple
          options={skills.filter(
            skill => !searchSkills.map(sSkill => sSkill.id).includes(skill.id)
          )}
          value={searchSkills ? searchSkills : []}
          onChange={onSkillsChange}
          isOptionEqualToValue={(option, value) =>
            value ? value.id === option.id : false
          }
          getOptionLabel={option => option.name}
          renderInput={params => (
            <TextField
              {...params}
              className="fullWidth"
              label="Skills"
              placeholder="Choose skills for radar chart"
            />
          )}
        />
        <Button
          onClick={() => {
            if (!searchSkills.length) {
              window.snackDispatch({
                type: UPDATE_TOAST,
                payload: {
                  severity: 'error',
                  toast: 'Must select a skill'
                }
              });
              return;
            }
            handleSearch(createRequest(editedSearchRequest));
          }}
          color="primary"
        >
          Run Search
        </Button>
      </div>
      {showRadar && (
        <div>
          <div style={{ height: '400px' }}>
            <MemberSkillRadar
              data={chartData || []}
              members={selectedMembers}
            />
          </div>
          <div className="search-results">
            <Typography variant="h5" fontWeight="bold">
              Search Results
            </Typography>
            {!searchResultsCopy.length && (
              <Typography variant="body1" color="textSecondary">
                No Matches
              </Typography>
            )}
            <SearchResults searchResults={searchResultsCopy} />
          </div>
          <div className="search-results">
            <Typography variant="h5" fontWeight="bold">
              All Employees With Selected Skills
            </Typography>
            <SearchResults searchResults={allSearchResults} />
          </div>
        </div>
      )}
    </div>
  );
};

export default TeamSkillReportPage;
