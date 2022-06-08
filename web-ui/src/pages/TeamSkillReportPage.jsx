import React, { useContext, useState } from "react";

import { reportSkills } from "../api/memberskill.js";
import SearchResults from "../components/search-results/SearchResults";
import MyResponsiveRadar from "../components/radar/Radar";
import { UPDATE_TOAST } from "../context/actions";
import { AppContext } from "../context/AppContext";
import {
  selectOrderedSkills,
  selectCsrfToken,
  selectOrderedCurrentMemberProfiles,
  selectSkill,
} from "../context/selectors";
import { levelMap } from "../context/util";

import { Button, TextField } from "@mui/material";

import Autocomplete from '@mui/material/Autocomplete';

import { Group, GroupAdd } from "@mui/icons-material";

import "./TeamSkillReportPage.css";

const TeamSkillReportPage = () => {
  const { state } = useContext(AppContext);
  const { teams } = state;

  const csrf = selectCsrfToken(state);
  const skills = selectOrderedSkills(state);
  const memberProfiles = selectOrderedCurrentMemberProfiles(state);

  const [selectedMembers, setSelectedMembers] = useState([]);
  const [selectedTeam, setSelectedTeam] = useState(null);
  const [searchResults, setSearchResults] = useState([]);
  const [allSearchResults, setAllSearchResults] = useState([]);
  const [searchSkills, setSearchSkills] = useState([]);
  const [editedSearchRequest, setEditedSearchRequest] = useState([]);
  const [showRadar, setShowRadar] = useState(false);
  const [showExistingTeam, setShowExistingTeam] = useState(false);
  const [showAdHocTeam, setShowAdHocTeam] = useState(true);

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
      setAllSearchResults(memberSkillsFound);
      setSearchResults(
        memberSkillsFound.filter((mSkill) =>
          selectedMembers.some((member) => member.id === mSkill.id)
        )
      );
    } else {
      setSearchResults([]);
      setAllSearchResults([]);
    }
    setShowRadar(true);
  };

  function skillsToSkillLevel(skills) {
    return skills.map((skill, index) => {
      let skillLevel = {
        id: skill.id,
        level: skill.skilllevel,
      };
      return skillLevel;
    });
  }

  function createRequest(editedSearchRequest) {
    let newSearchRequest = {
      skills: skillsToSkillLevel(searchSkills),
      members: [],
    };
    setEditedSearchRequest(newSearchRequest);
    return newSearchRequest;
  }

  function onSkillsChange(event, newValue) {
    let skillsCopy = newValue.sort((a, b) => a.name.localeCompare(b.name));
    setSearchSkills([...skillsCopy]);
  }

  const onMemberChange = (event, newValue) => {
    setSelectedMembers(newValue);
  };

  const onTeamChange = (event, newValue) => {
    setSelectedTeam(newValue);
    setSelectedMembers(
      // since teamMembers has an id and a memberId
      newValue.teamMembers.map((member) => ({
        ...member,
        id: member.memberId || member.id,
      }))
    );
  };

  const handleExistingTeam = () => {
    setSelectedMembers([]);
    setSearchSkills([]);
    setSearchResults([]);
    setShowExistingTeam(true);
    setShowAdHocTeam(false);
    setShowRadar(false);
  };

  const handleAdHocTeam = () => {
    setSelectedMembers([]);
    setSearchSkills([]);
    setSearchResults([]);
    setShowExistingTeam(false);
    setShowAdHocTeam(true);
    setShowRadar(false);
  };

  const skillMap = {};

  const selectedMembersCopy = selectedMembers.map((member) => ({ ...member }));
  let searchResultsCopy = searchResults.map((result) => ({ ...result }));
  const filteredResults = searchResultsCopy.filter((result) => {
    return selectedMembersCopy.some((member) => {
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
      <div className="filter-section">
        <div className="button-parent">
          <div className="button">
            <h5>Existing Team</h5>
            <div onClick={handleExistingTeam}>
              <div
                className={
                  showExistingTeam ? "active circle" : "inactive circle"
                }
              >
                <Group />
              </div>
            </div>
          </div>
          <div className="button">
            <h5>Ad Hoc Team</h5>
            <div onClick={handleAdHocTeam}>
              <div
                className={showAdHocTeam ? "active circle" : "inactive circle"}
              >
                <GroupAdd />
              </div>
            </div>
          </div>
        </div>
        <div className="team-skill-autocomplete">
          {showAdHocTeam ? (
            <div>
              <Autocomplete
                id="member"
                multiple
                options={memberProfiles}
                value={selectedMembers || []}
                onChange={onMemberChange}
                isOptionEqualToValue={(option, value) =>
                  value ? value.id === option.id : false
                }
                getOptionLabel={(option) => option.name}
                renderInput={(params) => (
                  <TextField
                    {...params}
                    className="fullWidth"
                    label="Members"
                    placeholder="Choose members for radar chart"
                  />
                )}
              />
            </div>
          ) : showExistingTeam ? (
            <Autocomplete
              id="team"
              options={teams}
              value={selectedTeam || []}
              onChange={onTeamChange}
              isOptionEqualToValue={(option, value) =>
                value ? value.id === option.id : false
              }
              getOptionLabel={(option) => option.name}
              renderInput={(params) => (
                <TextField
                  {...params}
                  className="fullWidth"
                  label="Team"
                  placeholder="Choose a team for radar chart"
                />
              )}
            />
          ) : null}
          <Autocomplete
            id="skillSelect"
            multiple
            options={skills.filter(
              (skill) =>
                !searchSkills.map((sSkill) => sSkill.id).includes(skill.id)
            )}
            value={searchSkills ? searchSkills : []}
            onChange={onSkillsChange}
            isOptionEqualToValue={(option, value) =>
              value ? value.id === option.id : false
            }
            getOptionLabel={(option) => option.name}
            renderInput={(params) => (
              <TextField
                {...params}
                className="fullWidth"
                label="Skills"
                placeholder="Choose skills for radar chart"
              />
            )}
          />
          <div className="skills-search halfWidth">
            <Button
              onClick={() => {
                if (!searchSkills.length) {
                  window.snackDispatch({
                    type: UPDATE_TOAST,
                    payload: {
                      severity: "error",
                      toast: "Must select a skill",
                    },
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
        </div>
      </div>
      {showRadar && (
        <div>
          <div style={{ height: "400px" }}>
            <MyResponsiveRadar
              data={chartData || []}
              selectedMembers={selectedMembers}
            />
          </div>
          <div className="search-results">
            <h2>Search Results</h2>
            {!searchResultsCopy.length && <h4>No Matches</h4>}
            <SearchResults searchResults={searchResultsCopy} />
          </div>
          {showAdHocTeam && (
            <div className="search-results">
              <h2>All Employees With Selected Skills</h2>
              <SearchResults searchResults={allSearchResults} />
            </div>
          )}
        </div>
      )}
    </div>
  );
};

export default TeamSkillReportPage;
