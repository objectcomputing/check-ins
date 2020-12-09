import React, { useContext, useEffect, useState } from "react";

import { AppContext, UPDATE_SKILLS } from "../context/AppContext";
import { getPendingSkills } from "../api/skill";
import PendingSkillsCard from "../components/pending_skills/PendingSkillsCard";
import PendingSkillsModal from "../components/pending_skills/PendingSkillsModal";

import { Button } from "@material-ui/core";

import "./PendingSkillsPage.css";

const PendingSkillsPage = () => {
  const { state, dispatch } = useContext(AppContext);
  const { csrf, skills } = state;

  const [allSkills, setSkills] = useState(skills);
  const [open, setOpen] = useState(false);

  const handleOpen = () => setOpen(true);

  const handleClose = () => setOpen(false);

  useEffect(() => {
    const getSkills = async () => {
      let res = await getPendingSkills(csrf);
      if (!res || !res.payload || !res.payload.data) {
        return;
      }
      let data = res.payload.data;
      const copy = [...allSkills];
      data.map(async (skill) => {
        let existingSkill = copy.find((s) => s.id === skill.id);
        if (!existingSkill) {
          copy.push(skill);
          existingSkill = skill;
        }
      });
      setSkills(copy);
      dispatch({ type: UPDATE_SKILLS, payload: copy });
    };
    if (csrf) {
      getSkills();
    }
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [csrf]);

  return (
    <div className="pending-skills-page">
      <Button onClick={handleOpen}>Combine Skills</Button>
      <PendingSkillsModal open={open} onClose={handleClose} />
      <div>
        {allSkills.map(
          (skill) =>
            skill.pending && (
              <PendingSkillsCard
                key={"pending-skill-" + skill.id}
                pendingSkill={skill}
              />
            )
        )}
      </div>
    </div>
  );
};

export default PendingSkillsPage;
