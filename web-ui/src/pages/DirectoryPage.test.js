import React from "react";
import DirectoryPage from "./DirectoryPage";
import { AppContextProvider } from "../context/AppContext";

it("renders correctly", () => {
  snapshot(
    <AppContextProvider>
      <DirectoryPage />
    </AppContextProvider>
  );
});
