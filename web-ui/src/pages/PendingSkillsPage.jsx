import React, { useContext, useState } from "react";

import { AppContext, selectPendingSkills } from "../context/AppContext";
import PendingSkillsCard from "../components/pending_skills/PendingSkillsCard";
import CombineSkillsModal from "../components/pending_skills/CombineSkillsModal";

import { Button, TextField } from "@material-ui/core";

import "./PendingSkillsPage.css";

const PendingSkillsPage = (props) => {
  const { state } = useContext(AppContext);
  const { pendingSkill } = state;
  const [searchText, setSearchText] = useState("");

  const [open, setOpen] = useState(false);

  const handleOpen = () => setOpen(true);

  const handleClose = () => setOpen(false);

  return (
    <div className="pending-skills-page">
      <Button onClick={handleOpen}>Combine Skills</Button>
      <TextField
        className="fullWidth"
        label="Search Skills"
        placeholder="Skill Name"
        style={{ marginBottom: "1rem" }}
        value={searchText}
        onChange={(e) => {
          setSearchText(e.target.value);
        }}
      />
      <CombineSkillsModal open={open} onClose={handleClose} />
      <div>
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
