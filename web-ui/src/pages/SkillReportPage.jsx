import React, { useContext, useState } from "react";

import { AppContext } from "../context/AppContext";
import { reportSkills } from "../api/memberskill.js";
import { selectOrderedSkills, selectCsrfToken, selectOrderedMemberProfiles } from "../context/selectors";

import { Button, Card, CardHeader, Chip, List, ListItem, TextField } from "@material-ui/core";
import Autocomplete from "@material-ui/lab/Autocomplete";
import PersonIcon from "@material-ui/icons/Person";

import "./SkillReportPage.css";

const SkillReportPage = (props) => {
  const { state } = useContext(AppContext);
  const csrf = selectCsrfToken(state);
  const skills = selectOrderedSkills(state);
  const memberProfiles = selectOrderedMemberProfiles(state);
  const [ searchResults, setSearchResults ] = useState([]);
  const [ searchRequestDTO ] = useState([]);
  const [ searchMembers, setSearchMembers ] = useState([]);
  const [ searchSkills, setSearchSkills ] = useState([]);
  const [editedSearchRequest, setEditedSearchRequest] = useState(searchRequestDTO);

  const handleSearch = async (searchRequestDTO) => {
    let res = await reportSkills(searchRequestDTO, csrf);
    let memberSkillsToSearch;
    if (res && res.payload) {
        memberSkillsToSearch =
            res.payload.data.teamMembers && !res.error ?
            res.payload.data.teamMembers : undefined;
    }
    if (memberSkillsToSearch) {
        getSkillNames(memberSkillsToSearch);
    } else {
        setSearchResults(undefined);
    }
    console.log(memberSkillsToSearch)
  }

  function getSkillNames(results) {
     results.forEach(teamMember => {
         teamMember.skills = teamMember.skills.map((memberSkill, index) => {
            let skill = skills.find((skill) => skill.id === memberSkill.id);
            let namedMemberSkill = {
                id: memberSkill.id,
                level: memberSkill.level,
                name: skill.name
            }
            return namedMemberSkill;
        })
      })
      setSearchResults(results);
  }



  function membersToIdArray(members) {
        return members.map((member) => {
            return member.id;
        })
      }

  function skillsToSkillLevelDTO(skills) {
    return skills.map((skill, index) => {
      let skillLevel = {
        id: skill.id,
        level: skill.skilllevel
      }
      return skillLevel;
    })
  }

  function createRequestDTO(editedSearchRequest) {
    let skills = skillsToSkillLevelDTO(searchSkills);
    let members = membersToIdArray(searchMembers);
    let inclusive = false;
    let newSearchRequest = {
      skills: skills,
      members: members,
      inclusive: inclusive,
    }
    setEditedSearchRequest(newSearchRequest);
    return newSearchRequest;
  }

  const onSkillsChange = (event, newValue) => {
    let skillsCopy = newValue.sort((a,b) =>
        a.name.localeCompare(b.name))
    setSearchSkills([...skillsCopy]);
  };

  const onMembersChange = (event, newValue) => {
    setSearchMembers([...newValue]);
    let membersCopy = newValue.sort((a,b) =>
        a.lastName.localeCompare(b.lastName))
    setSearchMembers([...membersCopy])
  };

  const reset = () => {
    setSearchSkills([]);
    setSearchMembers([]);
  };

  const chip = (skill) => {
    let level = skill.level
    let skillLevel = level.charAt(0) + level.slice(1).toLowerCase();
    let chipLabel = skill.name + " - " + skillLevel;
    return (
      <Chip
        label={chipLabel}
      ></Chip>
    );
  };

  return (
    <div className="skills-report-page">
      <div className="SkillReportModal">
        <h2>{'Choose Search Options'}</h2>
          <Autocomplete
            id="skillSelect"
            multiple
            options={skills.filter((skill) =>
              !searchSkills.map((sSkill) =>
                  sSkill.id).includes(skill.id)
            )}
            value={
              searchSkills
                ? searchSkills
                : []
            }
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
          <Autocomplete
            id="memberSelect"
            multiple
            options={memberProfiles.filter((memberProfile) =>
              !searchMembers.map((sMember) =>
                sMember.id).includes(memberProfile.id)
            )}
            value={
              searchMembers
                ? searchMembers
                : []
            }
            onChange={onMembersChange}
            getOptionLabel={(option) => option.name}
            getOptionSelected={(option, value) =>
              value ? value.id === option.id : false
            }
            renderInput={(params) => (
              <TextField
                {...params}
                className="fullWidth"
                label="Members *"
                placeholder="Add a member..."
              />
            )}
          />
          <div className="SkillsSearch-actions fullWidth">
            <Button
              onClick={() => {
                handleSearch(createRequestDTO(editedSearchRequest));
                reset();
              }}
              color="primary"
            >
              Run Search
            </Button>

          </div>
        </div>
        <div className="results-section">
              <CardHeader
                  title="Search Results"
                  titleTypographyProps={{variant: "h5", component: "h2"}}
                  action={null}
              />
              <List>
              {(searchResults === undefined) ?
                <ListItem >
                  No Results
                </ListItem> :
                searchResults.map((teamMember, index) => {
                  return (
                  <Card>
                  <CardHeader
                    avatar={<PersonIcon />}
                    title={teamMember.name}
                    titleTypographyProps={{variant: "h5", component: "h2"}}
                    action={null}/>
                    <ListItem key={`teamMember-${teamMember.name}`} >
                      {teamMember.skills.map((skill, index) => {
                        return (
                            <div>{chip(skill)}</div>
                        );
                      })
                      }
                    </ListItem>
                  </Card>
                  );
              })}
              </List>
        </div>
    </div>
  );
};

export default SkillReportPage;
