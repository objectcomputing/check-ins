import React from "react";
import {AppContextProvider} from "../context/AppContext";
import SkillCategoryEditPage from "./SkillCategoryEditPage";
import {BrowserRouter} from "react-router-dom";

it("renders correctly", () => {
  snapshot(
    <AppContextProvider>
      <BrowserRouter>
        <SkillCategoryEditPage />
      </BrowserRouter>
    </AppContextProvider>
  );
});