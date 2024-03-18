import React from "react";
import EditSkillsPage from "./EditSkillsPage";
import { AppContextProvider } from "../context/AppContext";
import {BrowserRouter} from "react-router-dom";

it("renders correctly", () => {
  snapshot(
    <AppContextProvider>
      <BrowserRouter>
        <EditSkillsPage />
      </BrowserRouter>
    </AppContextProvider>
  );
});
