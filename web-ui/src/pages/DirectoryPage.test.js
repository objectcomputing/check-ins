import React from "react";
import PeoplePage from "./PeoplePage";
import { AppContextProvider } from "../context/AppContext";

it("renders correctly", () => {
  snapshot(
    <AppContextProvider>
      <PeoplePage />
    </AppContextProvider>
  );
});
