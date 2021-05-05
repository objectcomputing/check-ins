import React from "react";
import MemberProfilePage from "./MemberProfilePage";
import { AppContextProvider } from "../context/AppContext";
import { createMemoryHistory } from "history";
import { Router } from "react-router-dom";

it("renders correctly", () => {
  const history = createMemoryHistory(`/profile/12345`);
  snapshot(
    <Router history={history}>
      <AppContextProvider>
        <MemberProfilePage />
      </AppContextProvider>
    </Router>
  );
});
