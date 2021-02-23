import React from "react";
import TeamMemberContainer from "../components/team-member/TeamMemberContainer";

const MyTeamPage = () => {
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
        <TeamMemberContainer />
      </div>
    </div>
  );
};

export default MyTeamPage;
