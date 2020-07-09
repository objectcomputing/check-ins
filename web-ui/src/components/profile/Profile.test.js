import React from "react";
import Profile from "./Profile";
import { SkillsContextProvider } from "../../context/SkillsContext";

it("renders correctly", () => {
  snapshot(
    <SkillsContextProvider value={null}>
      <Profile />
    </SkillsContextProvider>
  );
});

it("renders image_url", () => {
  snapshot(
    <SkillsContextProvider value={null}>
      <Profile image_url="http://someurl.com/das.png" />
    </SkillsContextProvider>
  );
});
