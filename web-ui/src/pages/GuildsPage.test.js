import React from "react";
import GuildsPage from "./GuildsPage";
import { AppContextProvider } from "../context/AppContext";

it("renders correctly", () => {
  snapshot(
    <AppContextProvider>
      <GuildsPage />
    </AppContextProvider>
  );
});
