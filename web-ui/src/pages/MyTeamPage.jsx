import React from "react";
import TeamMemberContainer from "../components/team-member/TeamMemberContainer";

const testProfile = [
  { name: "holmes" },
  { name: "homie" },
  { name: "homie g" },
  { name: "Jes" },
  { name: "Michael" },
  { name: "Holly" },
  { name: "Mohit" },
  { name: "Pramukh" },
];

const HomePage = () => {
  return (
    <div
      style={{
        display: "flex",
        justifyContent: "center",
        flexDirection: "column",
      }}
    >
      <h3>Professional Development @ OCI</h3>
      <div
        style={{
          display: "flex",
          justifyContent: "center",
          flexDirection: "row",
        }}
      >
        <TeamMemberContainer profiles={testProfile} />
      </div>
    </div>
  );
};

export default HomePage;
