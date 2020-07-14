import React from "react";
import TeamMemberContainer from "./TeamMemberContainer";
import { SkillsContextProvider } from "../../context/SkillsContext";

const testProfile = [
  { name: "holmes", image_url: "" },
  { name: "homie", image_url: "" },
];

it("renders correctly", () => {
  snapshot(
    <SkillsContextProvider value={null}>
      <TeamMemberContainer profiles={testProfile} />
    </SkillsContextProvider>
  );
});
