import React from "react";
import EditSkillsPage from "./EditSkillsPage";
import { AppContextProvider } from "../context/AppContext";

it("renders correctly", () => {
  snapshot(
    <AppContextProvider>
      <EditSkillsPage />
    </AppContextProvider>
  );
});
