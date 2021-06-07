import React, { useContext, useState } from "react";

import { AppContext } from "../context/AppContext";
import { reportSkills } from "../api/memberskill.js";
import { levelMap } from "../context/util";
import SearchResults from "../components/search-results/SearchResults";
import MyResponsiveRadar from "../components/radar/Radar";

import {
  selectOrderedSkills,
  selectCsrfToken,
  selectOrderedMemberProfiles,
  selectSkill,
} from "../context/selectors";

import { Button, TextField } from "@material-ui/core";

import Autocomplete from "@material-ui/lab/Autocomplete";

import { Group, GroupAdd } from "@material-ui/icons";

import "./TeamSkillReportPage.css";

const TeamSkillReportPage = (props) => {
  const { state } = useContext(AppContext);

  const csrf = selectCsrfToken(state);
  const skills = selectOrderedSkills(state);
  const memberProfiles = selectOrderedMemberProfiles(state);
  
  const [selectedMembers, setSelectedMembers] = useState([]);
  const [searchResults, setSearchResults] = useState([]);
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
      setSearchResults(memberSkillsFound);
    } else {
      setSearchResults([]);
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
    let skills = skillsToSkillLevel(searchSkills);
    let members = [];
    let newSearchRequest = {
      skills: skills,
      members: members,
    };
    setEditedSearchRequest(newSearchRequest);
    return newSearchRequest;
  }

  function onSkillsChange(event, newValue) {
    let skillsCopy = newValue.sort((a, b) => a.name.localeCompare(b.name));
    setSearchSkills([...skillsCopy]);
  }

  const onMemberChange = (event, newValue) => {
    console.log({ newValue });
    setSelectedMembers(newValue);
  };

  console.log({ selectedMembers });

  const skillMap = {};

  const selectedMembersCopy = [...selectedMembers];
  const searchResultsCopy = [...searchResults];
  const filteredResults = searchResultsCopy.filter((result) => {
    result.name = result.name.split(" ")[0];
    return selectedMembersCopy.some((member) => {
      return result.name === member.firstName;
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
    member.name = member.name.split(" ")[0];
    for (const data of chartData) {
      if (!data[member.name]) {
        data[member.name] = 0;
      }
    }
  }

  console.log({ memberProfiles });

  return (
    <div className="team-skill-report-page">
      <div className="filter-section">
        {showAdHocTeam ? (
          <div className="team-skill-autocomplete">
            <Autocomplete
              id="pdlSelect"
              multiple
              options={memberProfiles}
              value={selectedMembers || []}
              onChange={onMemberChange}
              getOptionSelected={(option, value) =>
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
            <Autocomplete
              id="skillSelect"
              multiple
              options={skills.filter(
                (skill) =>
                  !searchSkills.map((sSkill) => sSkill.id).includes(skill.id)
              )}
              value={searchSkills ? searchSkills : []}
              onChange={onSkillsChange}
              getOptionSelected={(option, value) =>
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
                  handleSearch(createRequest(editedSearchRequest));
                }}
                color="primary"
              >
                Run Search
              </Button>
            </div>
          </div>
        ) : null}
        <div className="button-parent">
          <div className="button">
            <h5>Existing Team</h5>
            <div
              onClick={() => {
                setShowExistingTeam(true);
                setShowAdHocTeam(false);
              }}
            >
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
            <div
              onClick={() => {
                setShowExistingTeam(false);
                setShowAdHocTeam(true);
              }}
            >
              <div
                className={showAdHocTeam ? "active circle" : "inactive circle"}
              >
                <GroupAdd />
              </div>
            </div>
          </div>
        </div>
      </div>

      <div>
        {showRadar && (
          <div style={{ height: "400px" }}>
            <MyResponsiveRadar
              data={chartData || []}
              selectedMembers={selectedMembers}
            />
          </div>
        )}
        <SearchResults searchResults={searchResultsCopy} />
      </div>
    </div>
  );
};

export default TeamSkillReportPage;
