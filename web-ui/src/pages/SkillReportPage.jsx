import React, { useContext } from "react";

import { AppContext } from "../context/AppContext";
import SkillSection from "../components/skills/SkillSection";
import { selectCurrentUser } from "../context/selectors";

import "./SkillReportPage.css";

const SkillReportPage = (props) => {
  const { state } = useContext(AppContext);
  const userProfile = selectCurrentUser(state);

  const { id } = userProfile;

  return (
    <div className="skills-report-page">
        <div className="skills-section">
          <SkillSection userId={id} />
        </div>
    </div>
  );
};

export default SkillReportPage;
