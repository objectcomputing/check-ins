import React, { useContext, useEffect, useState } from 'react';
import { AppContext } from '../../context/AppContext';
import {
  ADD_SKILL,
  DELETE_SKILL,
  UPDATE_MEMBER_SKILLS
} from '../../context/actions';
import { combineSkill } from '../../api/skill';
import { getMemberSkills } from '../../api/memberskill';

import Autocomplete from '@mui/material/Autocomplete';
import { Checkbox, Modal, TextField } from '@mui/material';
import { Button } from '@mui/material';
import CheckBoxOutlineBlankIcon from '@mui/icons-material/CheckBoxOutlineBlank';
import CheckBoxIcon from '@mui/icons-material/CheckBox';

import './EditSkills.css';

const icon = <CheckBoxOutlineBlankIcon fontSize="small" />;
const checkedIcon = <CheckBoxIcon fontSize="small" />;

const EditSkillsModal = ({ open, onClose, skillsToEdit }) => {
  const { state, dispatch } = useContext(AppContext);
  const { csrf } = state;
  const [pendingSkills, setPendingSkills] = useState([]);
  const [combinedSkill, setCombinedSkill] = useState({
    name: '',
    description: '',
    skillsToCombine: []
  });
  const { skillsToCombine, name } = combinedSkill;

  useEffect(() => {
    setPendingSkills(skillsToEdit);
  }, [skillsToEdit, state]);

  const handleSelections = (event, values) => {
    let skills = values.map(skill => {
      return skill.id;
    });
    setCombinedSkill({
      ...combinedSkill,
      skillsToCombine: skills
    });
  };

  const close = () => {
    onClose();
    setCombinedSkill({
      name: '',
      description: '',
      skillsToCombine: ''
    });
  };

  const save = async () => {
    if (name && skillsToCombine && csrf) {
      const res = await combineSkill({ ...combinedSkill, pending: true }, csrf);
      const data =
        res && res.payload && res.payload.data ? res.payload.data : null;
      if (!data) {
        return;
      }
      skillsToCombine.forEach(id =>
        dispatch({ type: DELETE_SKILL, payload: id })
      );
      dispatch({ type: ADD_SKILL, payload: data });
      const result = await getMemberSkills(csrf);
      const memberSkills =
        result && result.payload && result.payload.data
          ? result.payload.data
          : null;
      if (memberSkills) {
        dispatch({ type: UPDATE_MEMBER_SKILLS, payload: memberSkills });
      }
      close();
    }
  };

  return (
    <Modal open={open} onClose={close}>
      {pendingSkills && (
        <div className="combine-skills-modal">
          <Autocomplete
            multiple
            options={pendingSkills}
            disableCloseOnSelect
            getOptionLabel={option => option.name}
            onChange={handleSelections}
            renderOption={(props, option, { selected }) => (
              <li {...props}>
                <Checkbox
                  icon={icon}
                  checkedIcon={checkedIcon}
                  style={{ marginRight: 8 }}
                  checked={selected}
                />
                {option.name}
              </li>
            )}
            style={{ width: 500 }}
            renderInput={params => (
              <TextField
                {...params}
                label="Choose Skills to Combine"
                variant="outlined"
              />
            )}
          />
          <TextField
            className="halfWidth"
            label="Name"
            onChange={e => {
              setCombinedSkill({ ...combinedSkill, name: e.target.value });
            }}
            value={combinedSkill ? combinedSkill.name : ''}
            variant="outlined"
          />
          <TextField
            className="halfWidth"
            label="Description"
            multiline
            onChange={e => {
              setCombinedSkill({
                ...combinedSkill,
                description: e.target.value
              });
            }}
            value={combinedSkill ? combinedSkill.description : ''}
            variant="outlined"
          />
          <div className="fullWidth">
            <Button onClick={close} color="secondary">
              Cancel
            </Button>
            <Button onClick={save} color="primary">
              Save Skill
            </Button>
          </div>
        </div>
      )}
    </Modal>
  );
};
export default EditSkillsModal;
