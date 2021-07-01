import React, { useContext, useState } from "react";

// import { reportSkills } from "../api/memberskill.js";
import { getAnniversary } from "../api/birthdayanniversary.js";
import { getBirthday } from "../api/birthdayanniversary.js";
import SearchBirthdayResults from "../components/search-results/SearchBirthdayResults";
import SearchAnniversaryResults from "../components/search-results/SearchAnniversaryResults";

import MyResponsiveRadar from "../components/radar/Radar";
import { UPDATE_TOAST } from "../context/actions";
import { AppContext } from "../context/AppContext";
import {
  selectOrderedSkills,
  selectCsrfToken,
  selectOrderedMemberProfiles,
  selectSkill,
} from "../context/selectors";
import { levelMap } from "../context/util";

import { Button, TextField } from "@material-ui/core";

import Autocomplete from "@material-ui/lab/Autocomplete";

import { Group, GroupAdd } from "@material-ui/icons";

import "./TeamSkillReportPage.css";

const months = [
  {title: 'January'},
  {title:'February'},
  {title:'March'},
  {title:'April'},
  {title:'May'},
  {title:'June'},
  {title:'July'},
  {title:'August'},
  {title:'September'},
  {title:'October'},
  {title:'November'},
  {title:'December'},
];

const BirthdayAnniversaryReportPage = () => {
  const { state } = useContext(AppContext);
  const { teams } = state;

  const csrf = selectCsrfToken(state);
  const skills = selectOrderedSkills(state);
  const memberProfiles = selectOrderedMemberProfiles(state);

  const [selectedMembers, setSelectedMembers] = useState([]);
  const [selectedTeam, setSelectedTeam] = useState(null);
  const [searchResults, setSearchResults] = useState([]);
  const [allSearchResults, setAllSearchResults] = useState([]);
  const [searchSkills, setSearchSkills] = useState([]);
  const [editedSearchRequest, setEditedSearchRequest] = useState([]);
  const [showRadar, setShowRadar] = useState(false);
  const [showExistingTeam, setShowExistingTeam] = useState(false);
  const [showAdHocTeam, setShowAdHocTeam] = useState(true);

  const handleSearch = async (searchMembers) => {
//     let res = await getBirthday(searchMembers, csrf);
    let res = await getAnniversary(searchMembers, csrf);
    let memberSkillsFound;
    console.log(searchMembers);
    console.log(res.payload.data);
    if (res && res.payload) {
      memberSkillsFound =
        res.payload.data && !res.error
          ? res.payload.data
          : undefined;
    }
    if (memberSkillsFound && memberProfiles) {
      setAllSearchResults(memberSkillsFound);
      console.log(allSearchResults);
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
    let newSearchRequest ="january";
    setEditedSearchRequest(newSearchRequest);
    return newSearchRequest;
  }

//   function createRequest(editedSearchRequest) {
//     let newSearchRequest = {
//       skills: skillsToSkillLevel(searchSkills),
//       members: [],
//     };
//     setEditedSearchRequest(newSearchRequest);
//     return newSearchRequest;
//   }

  function onSkillsChange(event, newValue) {
    let skillsCopy = newValue;
    setSearchSkills([...skillsCopy]);
  }

  const handleBirthdaySearch = () => {
    setSelectedMembers([]);
    setSearchSkills([]);
    setSearchResults([]);
    setShowExistingTeam(true);
    setShowAdHocTeam(false);
    setShowRadar(false);
  };

  const handleAnniversariesSearch = () => {
    setSelectedMembers([]);
    setSearchSkills([]);
    setSearchResults([]);
    setShowExistingTeam(false);
    setShowAdHocTeam(true);
    setShowRadar(false);
  };

  const skillMap = {};

  const selectedMembersCopy = selectedMembers.map((member) => ({ ...member }));

  const chartData = Object.values(skillMap);

  for (const member of selectedMembersCopy) {
    member.name = member.name.split(" ")[0];
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
            <h5>BirthDays</h5>
            <div onClick={handleBirthdaySearch}>
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
            <h5>Anniversaries</h5>
            <div onClick={handleAnniversariesSearch}>
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
            </div>
          ) : showExistingTeam ? (
          <div>
            </div>
          ) : null}
          <Autocomplete
            id="monthSelect"
            multiple
            options={months}
            value={searchSkills ? searchSkills : []}
            onChange={onSkillsChange}
            getOptionSelected={(option, value) =>
              value ? value.id === option.id : false
            }
            getOptionLabel={(option) => option.title}
            renderInput={(params) => (
              <TextField
                {...params}
                className="fullWidth"
                label="Select a month"
                placeholder="Choose a month"
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
                      toast: "Must select a month",
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
          {showAdHocTeam && (
            <div className="search-results">
              <h2>All Employees With Selected Month</h2>
              <SearchAnniversaryResults searchResults={allSearchResults} />
            </div>
          )}
          {showExistingTeam && (
            <div className="search-results">
              <h2>All Employees With Selected Month</h2>
              <SearchBirthdayResults searchResults={allSearchResults} />
            </div>
          )}
        </div>
      )}

    </div>
  );
};

export default BirthdayAnniversaryReportPage;