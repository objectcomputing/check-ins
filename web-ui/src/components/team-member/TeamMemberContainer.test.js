import React from "react";
import TeamMembercontainer from "./TeamMemberContainer";

const testProfile = [
  { name: "holmes", image_url: "" },
  { name: "homie", image_url: "" },
];

it("renders correctly", () => {
  snapshot(<TeamMembercontainer profiles={testProfile} />);
});
