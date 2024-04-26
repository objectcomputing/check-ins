import React, { useContext, useEffect, useState } from 'react';

import { AppContext } from '../context/AppContext';
import { selectOrderedSkills, selectPendingSkills } from '../context/selectors';
import EditSkillsCard from '../components/edit_skills/EditSkillsCard';
import EditSkillsModal from '../components/edit_skills/EditSkillsModal';
import { Link } from 'react-router-dom';

import { Button, TextField } from '@mui/material';

import './EditSkillsPage.css';

const EditSkillsPage = () => {
  const { state } = useContext(AppContext);

  const [searchText, setSearchText] = useState('');
  const [showAllSkills, setShowAllSkills] = useState(false);
  const [open, setOpen] = useState(false);

  const allSkills = selectOrderedSkills(state);
  const pendingSkills = selectPendingSkills(state);

  useEffect(() => {
    const url = new URL(location.href);

    const combining = url.searchParams.get('combining');
    setOpen(combining === 'true');

    const search = url.searchParams.get('search') || '';
    setSearchText(search);

    const showAll = url.searchParams.get('showAll');
    setShowAllSkills(showAll === 'true');
  }, []);

  useEffect(() => {
    const url = new URL(location.href);
    let newUrl = url.origin + url.pathname;
    const params = {};
    if (searchText) params.search = searchText;
    if (open) params.combining = true;
    if (showAllSkills) params.showAll = true;
    if (Object.keys(params).length) {
      newUrl += '?' + new URLSearchParams(params).toString();
    }
    history.replaceState(params, '', newUrl);
  }, [open, searchText, showAllSkills]);

  const handleOpen = () => setOpen(true);

  const handleClose = () => setOpen(false);

  const toggleShowAllSkills = () => setShowAllSkills(!showAllSkills);

  return (
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
              onChange={toggleShowAllSkills}
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
  );
};

export default EditSkillsPage;
