import React, { useContext, useState } from "react";

import { AppContext } from "../context/AppContext";
import { reportSkills } from "../api/memberskill.js";
import { getAvatarURL } from "../api/api.js";
import { selectOrderedSkills, selectCsrfToken, selectOrderedMemberProfiles } from "../context/selectors";

import { Avatar, Button, Card, CardHeader, Chip, List, ListItem, TextField, Typography } from "@material-ui/core";
import Autocomplete from "@material-ui/lab/Autocomplete";

import "./SkillReportPage.css";

const SkillReportPage = (props) => {
  const { state } = useContext(AppContext);
  const csrf = selectCsrfToken(state);
  const skills = selectOrderedSkills(state);
  const memberProfiles = selectOrderedMemberProfiles(state);
  const [ searchResults, setSearchResults ] = useState([]);
  const [ searchRequestDTO ] = useState([]);
  const [ searchSkills, setSearchSkills ] = useState([]);
  const [editedSearchRequest, setEditedSearchRequest] = useState(searchRequestDTO);

  const handleSearch = async (searchRequestDTO) => {
    let res = await reportSkills(searchRequestDTO, csrf);
    let memberSkillsFound;
    if (res && res.payload) {
        memberSkillsFound =
            res.payload.data.teamMembers && !res.error ?
            res.payload.data.teamMembers : undefined;
    }
    if (memberSkillsFound && memberProfiles) {
        getSkillNames(memberSkillsFound);
        getMemberProfiles(memberSkillsFound);
    } else {
        setSearchResults(undefined);
    }
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

  function getMemberProfiles(results) {
    results = results.map((teamMember, index) => {
        let mappedTeamMember;
        let memberProfile = memberProfiles.find((mp) => mp.id === teamMember.id);
        mappedTeamMember = {
            id: memberProfile.id,
            name: memberProfile.name,
            workEmail: memberProfile.workEmail,
            title: memberProfile.title,
            skills: teamMember.skills
        }
        return mappedTeamMember;
    })
    setSearchResults(results)
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
    let members = [];
    let inclusive = false;
    let newSearchRequest = {
      skills: skills,
      members: members,
      inclusive: inclusive,
    }
    setEditedSearchRequest(newSearchRequest);
    return newSearchRequest;
  }

  function onSkillsChange (event, newValue) {
    let skillsCopy = newValue.sort((a,b) =>
        a.name.localeCompare(b.name))
    setSearchSkills([...skillsCopy]);
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
        <h2>{'Select desired skills...'}</h2>
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
        <div className="results-section">
              <CardHeader
                  title="Search Results"
                  titleTypographyProps={{variant: "h5", component: "h2"}}
                  action={null}
              />
              <List >
              {(searchResults === undefined) ?
                <ListItem key={`no-results`}>
                  No Results
                </ListItem> :
                searchResults.map((teamMember, index) => {
                  return (
                  <Card className={"member-skills-card"} key={`card-${teamMember.id}`}>
                  <CardHeader
                    key={`cardHeader-${teamMember.id}`}
                    title={
                      <Typography variant="h5" component="h2">
                        {teamMember.name}
                      </Typography>
                    }
                    subheader={<Typography color="textSecondary" component="h3">{teamMember.title}</Typography>}
                    disableTypography
                    avatar={
                      <Avatar
                        key={`avatar-${teamMember.name}`}
                        className={"large"}
                        src={getAvatarURL(teamMember.workEmail)}
                      />
                    }
                    />
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
