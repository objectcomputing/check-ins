import React, { useContext, useEffect, useState } from "react";

import { AppContext, UPDATE_SKILLS } from "../context/AppContext";
import { getSkillMembers } from "../api/memberskill";
import { getPendingSkills } from "../api/skill";
import PendingSkillsCard from "../components/pending_skills/PendingSkillsCard";

const PendingSkillsPage = () => {
  const { state, dispatch } = useContext(AppContext);
  const { selectMemberProfileById } = AppContext;
  const { memberProfiles, skills } = state;
  const [allSkills, setSkills] = useState(skills);

  useEffect(() => {
    const getSkills = async () => {
      let res = await getPendingSkills();
      if (!res || !res.payload || !res.payload.data) {
        return;
      }
      let data = res.payload.data;
      const copy = [...allSkills];
      await Promise.all(
        data.map(async (skill) => {
          let existingSkill = copy.find((s) => s.id === skill.id);
          if (!existingSkill) {
            copy.push(skill);
            existingSkill = skill;
          }
          let skillMembers = await getSkillMembers(skill.id);
          if (
            skillMembers &&
            skillMembers.payload &&
            skillMembers.payload.data &&
            skillMembers.payload.data.length > 0 &&
            memberProfiles
          ) {
            let members = [];
            skillMembers.payload.data.map((m) => {
              const { memberid } = m;
              const member = selectMemberProfileById(state)(memberid);
              if (member) members.push(member);
            });
            existingSkill.members = members;
          }
        })
      );
      setSkills(copy);
      dispatch({ type: UPDATE_SKILLS, payload: copy });
    };
    getSkills();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [memberProfiles]);

  return (
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
  );
};

export default PendingSkillsPage;
