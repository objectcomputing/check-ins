import React from "react";
import ErrorBoundaryPage from "./ErrorBoundaryPage";
import { AppContextProvider } from "../context/AppContext";

it("renders correctly", () => {
  snapshot(
    <AppContextProvider>
      <ErrorBoundaryPage />
    </AppContextProvider>
  );
});
