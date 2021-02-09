import React, { useContext, useState } from "react";

import { AppContext, selectPendingSkills } from "../context/AppContext";
import PendingSkillsCard from "../components/pending_skills/PendingSkillsCard";
import CombineSkillsModal from "../components/pending_skills/CombineSkillsModal";

import { Button, TextField } from "@material-ui/core";

import "./PendingSkillsPage.css";

const PendingSkillsPage = (props) => {
  const { state } = useContext(AppContext);
  
  const [searchText, setSearchText] = useState("");

  const [open, setOpen] = useState(false);

  const handleOpen = () => setOpen(true);

  const handleClose = () => setOpen(false);

  return (
    <div className="pending-skills-page">
        <div className="search">
          <TextField
            label="Search Skills"
            placeholder="Skill Name"
            value={searchText}
            onChange={(e) => {
              setSearchText(e.target.value);
            }}
          />
          <div className="combine-skills">
            <Button onClick={handleOpen}>Combine Skills</Button>
          </div>
        </div>

      <CombineSkillsModal open={open} onClose={handleClose} />
      <div className="pending-skills-list">
        {selectPendingSkills(state).map((skill) => 
          skill.name.toLowerCase().includes(searchText.toLowerCase()) ? (
            <PendingSkillsCard
              key={"pending-skill-" + skill.id}
              pendingSkill={skill}
            />
          ) : null
        )}
      </div>
    </div>
  );
};

export default PendingSkillsPage;
