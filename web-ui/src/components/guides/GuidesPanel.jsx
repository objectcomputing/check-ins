import React, { useContext } from "react";
import { AppContext } from "../../context/AppContext";
import "./GuidesPanel.css";
import GuideLink from "./GuideLink";

const GuidesPanel = () => {
  const { state } = useContext(AppContext);
  const { userProfile } = state;
  const isPdl =
    userProfile &&
    userProfile.role &&
    userProfile.role.length > 0 &&
    userProfile.role.includes("PDL");

  const teamMemberPDFs = [
    {
      name: "Expectations Discussion Guide for Team Members",
    },
    {
      name: "Expectations Worksheet",
    },
    {
      name: "Feedback Discussion Guide for Team Members",
    },
    {
      name: "Development Discussion Guide for Team Members",
    },
    {
      name: "Individual Development Plan",
    },
  ];

  const pdlPDFs = [
    {
      name: "Development Discussion Guide for PDLs",
    },
    {
      name: "Expectations Discussion Guide for PDLs",
    },
    {
      name: "Feedback Discussion Guide for PDLs",
    },
  ];

  return (
    <fieldset className="guide-container">
      <legend>Check-In Guides</legend>
      <div>
        {teamMemberPDFs.map((memberPDF) => (
          <GuideLink key={memberPDF.name} name={memberPDF.name} />
        ))}
        {isPdl &&
          pdlPDFs.map((pdlPDF) => (
            <GuideLink key={pdlPDF.name} name={pdlPDF.name} />
          ))}
      </div>
    </fieldset>
  );
};

export default GuidesPanel;
