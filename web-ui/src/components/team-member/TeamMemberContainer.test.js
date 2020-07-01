import React from "react";
import TeamMemberContainer from "./TeamMemberContainer";

const testProfile = [
  { name: "holmes", image_url: "" },
  { name: "homie", image_url: "" },
];

it("renders correctly", () => {
  snapshot(<TeamMemberContainer profiles={testProfile} />);
});
