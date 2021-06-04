import React, { useContext, useState } from "react";

import { AppContext } from "../context/AppContext";
import { selectOrderedSkills, selectPendingSkills } from "../context/selectors";
import EditSkillsCard from "../components/edit_skills/EditSkillsCard";
import EditSkillsModal from "../components/edit_skills/EditSkillsModal";

import { Button, TextField } from "@material-ui/core";

import "./EditSkillsPage.css";

const EditSkillsPage = (props) => {
  const { state } = useContext(AppContext);

  const [searchText, setSearchText] = useState("");
  const [showAllSkills, setShowAllSkills] = useState(false);
  const [open, setOpen] = useState(false);

  const allSkills = selectOrderedSkills(state);
  const pendingSkills = selectPendingSkills(state);

  const handleOpen = () => setOpen(true);

  const handleClose = () => setOpen(false);

  const handleClick = () => setShowAllSkills(!showAllSkills);

  return (
    <div className="pending-skills-page">
      <div className="search">
        <div>
          <TextField
            label="Search skills"
            placeholder="Skill Name"
            value={searchText}
            onChange={(e) => {
              setSearchText(e.target.value);
            }}
          />
          <div className="show-all-skills">
            <label htmlFor="all-skills">Show all skills</label>
            <input
              onClick={handleClick}
              id="all-skills"
              type="checkbox"
              value={showAllSkills}
            />
          </div>
        </div>
        <div className="combine-skills">
          <Button onClick={handleOpen}>Combine Skills</Button>
        </div>
      </div>

      <EditSkillsModal
        skillsToEdit={showAllSkills ? allSkills : pendingSkills}
        open={open}
        onClose={handleClose}
      />
      <div className="pending-skills-list">
        {!showAllSkills
          ? pendingSkills.map((skill) =>
              skill.name.toLowerCase().includes(searchText.toLowerCase()) ? (
                <EditSkillsCard
                  key={"pending-skill-" + skill.id}
                  skill={skill}
                />
              ) : null
            )
          : allSkills.map((skill) =>
              skill.name.toLowerCase().includes(searchText.toLowerCase()) ? (
                <EditSkillsCard
                  key={"pending-skill-" + skill.id}
                  skill={skill}
                />
              ) : null
            )}
      </div>
    </div>
  );
};

export default EditSkillsPage;
