import React, { useContext, useState } from "react";

import { AppContext } from "../../context/AppContext";
import { selectOrderedSkills } from "../../context/selectors";
// TODO - uncomment for members option
import { selectOrderedMemberProfiles } from "../../context/selectors";

import { Button } from "@material-ui/core";
import Modal from "@material-ui/core/Modal";
import TextField from "@material-ui/core/TextField";
import Autocomplete from "@material-ui/lab/Autocomplete";
import "./SkillsReportModal.css";

const SkillsReportModal = ({ searchRequestDTO = {}, open, onSave, onClose, headerText }) => {
  const { state } = useContext(AppContext);
  // TODO - uncomment for members option
  const memberProfiles = selectOrderedMemberProfiles(state);
  const [editedSearchRequest, setEditedSearchRequest] = useState(searchRequestDTO);
  const [ searchMembers, setSearchMembers ] = useState([]);
  const [ searchSkills, setSearchSkills ] = useState([]);
  const skills = selectOrderedSkills(state);

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

    // TODO - uncomment for members option
  const onMembersChange = (event, newValue) => {
    setSearchMembers([...newValue]);
    let membersCopy = newValue.sort((a,b) =>
        a.lastName.localeCompare(b.lastName))
    setSearchMembers([...membersCopy])
  };

  const readyToSearch = (editedSearchRequest) => {
    if (!editedSearchRequest) {
       return false;
    } else return true;
  };

  const close = () => {
    onClose();
    setSearchSkills([]);
    setSearchMembers([]);
  };

  return (
    <Modal
      open={open}
      onClose={close}
      aria-labelledby="skills-report-modal-title"
    >
      <div className="SkillsReportModal">
        <h2>{headerText}</h2>
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
{/*          TODO - uncomment for members option    */}
        <Autocomplete
          multiple
//           options={memberProfiles}
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
              label="Members"
              placeholder="Add a member..."
            />
          )}
        />
        <div className="EditTeamModal-actions fullWidth">
          <Button onClick={close} color="secondary">
            Cancel
          </Button>
          <Button
            disabled={!readyToSearch(editedSearchRequest)}
            onClick={() => {
              onSave(createRequestDTO(editedSearchRequest));
              close();
            }}
            color="primary"
          >
            Run Search
          </Button>
        </div>
      </div>
    </Modal>
  );
};

export default SkillsReportModal;
