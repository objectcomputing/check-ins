import React, { useContext, useState } from "react";

import { AppContext } from "../context/AppContext";
import { reportSkills } from "../api/memberskill.js";
import { selectOrderedSkills, selectCsrfToken } from "../context/selectors";

import SkillsReportModal from "../components/skills/SkillsReportModal";

import { Button, CardActions, CardHeader, Chip, List, ListItem } from "@material-ui/core";
import PersonIcon from "@material-ui/icons/Person";

import "./SkillReportPage.css";

const SkillReportPage = (props) => {
  const { state } = useContext(AppContext);
  const csrf = selectCsrfToken(state);
  const { userProfile } = state;
  const skills = selectOrderedSkills(state);
  const [ searchResults, setSearchResults ] = useState([]);
  const [open, setOpen] = useState(false);
  const [ searchRequestDTO ] = useState([]);
//   const isAdmin = true;
  const isAdmin =
          userProfile && userProfile.role && userProfile.role.includes("ADMIN");

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

  const handleOpen = () => setOpen(true);

  const handleClose = () => setOpen(false);

  const handleAction = (e, index) => handleOpen();

  const chip = (skill) => {
      return (
        <Chip
          label={skill.name}
        ></Chip>
      );
    };

  return (
    <div className="skills-report-page">
        <CardActions>
            {(isAdmin) &&
                <Button onClick={handleAction}>
                    Create Search
                </Button>}
        </CardActions>
        <SkillsReportModal
            searchRequestDTO={searchRequestDTO}
            open={open}
            onClose={handleClose}
            onSave={(editedSearchRequest) => {
                handleClose()
                handleSearch(editedSearchRequest)}
            }
            headerText='Choose Search Options'
        />
        <div className="results-section">
{/*             <Card> */}
              <CardHeader
                  avatar={<PersonIcon />}
                  title="Results"
                  titleTypographyProps={{variant: "h5", component: "h2"}}
                  action={null}/>
              <List>
              {(searchResults === undefined) ?
                <ListItem >
                  No Results
                </ListItem> :
                  searchResults.map((teamMember, index) => {
                    return (
                    <ListItem key={`teamMember-${teamMember.name}`} >
                      <div>{chip(teamMember)}</div>
                      {teamMember.skills.map((skill, index) => {
                        return (
                            <div>{chip(skill)}</div>
                        );
                      })
                      }
                    </ListItem>
                    );
                  })}
              </List>
{/*             </Card> */}
        </div>
    </div>
  );
};

export default SkillReportPage;
