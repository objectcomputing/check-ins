import React, { useContext, useState } from 'react';
import { Link } from 'react-router-dom';

import { AppContext } from '../context/AppContext';
import {
  selectOrderedSkills,
  selectPendingSkills,
  selectCanEditSkills,
  noPermission,
} from '../context/selectors';
import EditSkillsCard from '../components/edit_skills/EditSkillsCard';
import EditSkillsModal from '../components/edit_skills/EditSkillsModal';
import { useQueryParameters } from '../helpers/query-parameters';

import { Button, TextField } from '@mui/material';

import './EditSkillsPage.css';

const EditSkillsPage = () => {
  const { state } = useContext(AppContext);

  const [searchText, setSearchText] = useState('');
  const [showAllSkills, setShowAllSkills] = useState(false);
  const [open, setOpen] = useState(false);

  const allSkills = selectOrderedSkills(state);
  const pendingSkills = selectPendingSkills(state);

  const handleOpen = () => setOpen(true);

  const handleClose = () => setOpen(false);

  const handleClick = () => setShowAllSkills(!showAllSkills);

  useQueryParameters([
    {
      name: 'search',
      default: '',
      value: searchText,
      setter: setSearchText
    },
    {
      name: 'showAll',
      default: false,
      value: showAllSkills,
      setter: setShowAllSkills
    }
  ]);

  return selectCanEditSkills(state) ? (
    <div className="pending-skills-page">
      <div className="search">
        <div>
          <TextField
            label="Search skills"
            placeholder="Skill"
            fullWidth={true}
            value={searchText}
            onChange={e => {
              setSearchText(e.target.value);
            }}
          />
          <div className="show-all-skills">
            <label htmlFor="all-skills">Show all skills</label>
            <input
              onChange={handleClick}
              id="all-skills"
              type="checkbox"
              checked={showAllSkills}
            />
          </div>
        </div>
        <div className="skill-actions-container">
          <Button onClick={handleOpen}>Combine Skills</Button>
          <Link to="/admin/skill-categories">
            <Button>Categorize Skills</Button>
          </Link>
        </div>
      </div>

      <EditSkillsModal
        skillsToEdit={showAllSkills ? allSkills : pendingSkills}
        open={open}
        onClose={handleClose}
      />
      <div className="pending-skills-list">
        {!showAllSkills
          ? pendingSkills.map(skill =>
              skill.name.toLowerCase().includes(searchText.toLowerCase()) ? (
                <EditSkillsCard
                  key={'pending-skill-' + skill.id}
                  skill={skill}
                />
              ) : null
            )
          : allSkills.map(skill =>
              skill.name.toLowerCase().includes(searchText.toLowerCase()) ? (
                <EditSkillsCard
                  key={'pending-skill-' + skill.id}
                  skill={skill}
                />
              ) : null
            )}
      </div>
    </div>
  ) : (
    <h3>{noPermission}</h3>
  );
};

export default EditSkillsPage;
