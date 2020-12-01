import React, { useContext, useState } from "react";

import { AppContext, selectPendingSkills } from "../context/AppContext";
import PendingSkillsCard from "../components/pending_skills/PendingSkillsCard";
import PendingSkillsModal from "../components/pending_skills/PendingSkillsModal";

import { Button } from "@material-ui/core";

import "./PendingSkillsPage.css";

const PendingSkillsPage = () => {
  const { state } = useContext(AppContext);

  const [open, setOpen] = useState(false);

  const handleOpen = () => setOpen(true);

  const handleClose = () => setOpen(false);

  return (
    <div className="pending-skills-page">
      <Button onClick={handleOpen}>Combine Skills</Button>
      <PendingSkillsModal open={open} onClose={handleClose} />
      <div>
        {selectPendingSkills(state).map((skill) => (
          <PendingSkillsCard
            key={"pending-skill-" + skill.id}
            pendingSkill={skill}
          />
        ))}
      </div>
    </div>
  );
};

export default PendingSkillsPage;
