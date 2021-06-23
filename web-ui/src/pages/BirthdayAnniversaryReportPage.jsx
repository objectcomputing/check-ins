import React, { useContext, useState } from "react";

// import { reportSkills } from "../api/memberskill.js";
import { getAnniversary } from "../api/birthdayanniversary.js";
import SearchResults from "../components/search-results/SearchResults";
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
//     let res = await reportSkills(searchMembers, csrf);
    let res = await getAnniversary(searchMembers, csrf);
    let memberSkillsFound;
    if (res && res.payload) {
      memberSkillsFound = res.payload.data
      console.log(searchMembers);
      console.log(res.payload.data);
      console.log(res.payload.data.name);
//         res.payload.data.teamMembers && !res.error
//           ? res.payload.data.teamMembers
//           : undefined;
    }
    setAllSearchResults(memberSkillsFound);
//     if (memberSkillsFound && memberProfiles) {
//       setAllSearchResults(memberSkillsFound);
// //       setAllSearchResults([]);
//       setSearchResults(
//         memberSkillsFound.filter((mSkill) =>
//           selectedMembers.some((member) => member.id === mSkill.id)
//         )
//       );
//     } else {
//       setSearchResults([]);
//       setAllSearchResults([]);
//     }
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
    let skillsCopy = newValue;//newValue.sort((a, b) => a.name.localeCompare(b.name));
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
//   let searchResultsCopy = searchResults.map((result) => ({ ...result }));
//   const filteredResults = searchResultsCopy.filter((result) => {
//     result.name = result.name.split(" ")[0];
//     return selectedMembersCopy.some((member) => {
//       return result.name === member.firstName;
//     });
//   });

//   for (const result of filteredResults) {
//     const memberName = result.name;
//
//     for (const skill of result.skills) {
//       const { id } = skill;
//
//       const skillObj = selectSkill(state, skill.id);
//
//       if (skillObj) {
//         const skillName = skillObj.name;
//         let value = skillMap[id];
//         if (!value) {
//           value = { skill: skillName };
//           skillMap[id] = value;
//         }
//         value[memberName] = levelMap[skill.level];
//       } else {
//         console.error(`No skill with id ${id} found!`);
//       }
//     }
//   }

  const chartData = Object.values(skillMap);

  for (const member of selectedMembersCopy) {
    member.name = member.name.split(" ")[0];
    for (const data of chartData) {
      if (!data[member.name]) {
        data[member.name] = 0;
      }
    }
  }

  const onMonthChange = (event, newValue) => {
//     let extantPdls = filteredPdls || [];
//     newValue.forEach((val) => {
//       extantPdls = extantPdls.filter((pdl) => pdl.id !== val.id);
//     });
//     extantPdls = [...new Set(extantPdls)];
//     newValue = [...new Set(newValue)];
//     if (newValue.length > 0) {
//       setSelectedPdls(newValue);
//       setFilteredPdls([...newValue]);
//     } else {
//       setSelectedPdls([]);
//       setFilteredPdls(pdls);
//     }
  };
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
{/*               <Autocomplete */}
{/*                 id="member" */}
{/*                 multiple */}
{/*                 options={memberProfiles} */}
{/*                 value={selectedMembers || []} */}
{/*                 onChange={onMemberChange} */}
{/*                 getOptionSelected={(option, value) => */}
{/*                   value ? value.id === option.id : false */}
{/*                 } */}
{/*                 getOptionLabel={(option) => option.name} */}
{/*                 renderInput={(params) => ( */}
{/*                   <TextField */}
{/*                     {...params} */}
{/*                     className="fullWidth" */}
{/*                     label="Members" */}
{/*                     placeholder="Choose members for radar chart" */}
{/*                   /> */}
{/*                 )} */}
{/*               /> */}
            </div>
          ) : showExistingTeam ? (
          <div>
{/*             <Autocomplete */}
{/*               id="team" */}
{/*               options={teams} */}
{/*               value={selectedTeam || []} */}
{/*               onChange={onTeamChange} */}
{/*               getOptionSelected={(option, value) => */}
{/*                 value ? value.id === option.id : false */}
{/*               } */}
{/*               getOptionLabel={(option) => option.name} */}
{/*               renderInput={(params) => ( */}
{/*                 <TextField */}
{/*                   {...params} */}
{/*                   className="fullWidth" */}
{/*                   label="Team" */}
{/*                   placeholder="Choose a team for radar chart" */}
{/*                 /> */}
{/*               )} */}
{/*             /> */}
            </div>
          ) : null}
{/*          <Autocomplete */}
{/*            id="skillSelect" */}
{/*            multiple */}
{/*            options={months} */}
{/*            value={searchSkills ? searchSkills : []} */}
{/*            onChange={onMonthChange} */}
{/*            getOptionLabel={(option) => option.title} */}
{/*            renderInput={(params) => ( */}
{/*              <TextField */}
{/*                {...params} */}
{/*                label="Select Month..." */}
{/*                placeholder="Choose the month to display" */}
{/*              /> */}
{/*            )} */}
{/*          /> */}
          <Autocomplete
            id="skillSelect"
            multiple
//             options={skills.filter(
//               (skill) =>
//                 !searchSkills.map((sSkill) => sSkill.id).includes(skill.id)
//             )}
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
{/*           <div style={{ height: "400px" }}> */}
{/*             <MyResponsiveRadar */}
{/*               data={chartData || []} */}
{/*               selectedMembers={selectedMembers} */}
{/*             /> */}
{/*           </div> */}
{/*           <div className="search-results"> */}
{/*             <h2>Search Results</h2> */}
{/*             {!searchResultsCopy.length && <h4>No Matches</h4>} */}
{/*             <SearchResults searchResults={searchResultsCopy} /> */}
{/*           </div> */}
          {showAdHocTeam && (
            <div className="search-results">
              <h2>All Employees With Selected Month</h2>
              <SearchResults searchResults={allSearchResults} />
            </div>
          )}
        </div>
      )}
    </div>
  );
};

export default BirthdayAnniversaryReportPage;