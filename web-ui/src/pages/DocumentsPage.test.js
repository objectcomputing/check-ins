import React from "react";
import DocumentsPage from "./DocumentsPage";
import { AppContextProvider } from "../context/AppContext";

it("renders correctly", () => {
  snapshot(
    <AppContextProvider>
      <DocumentsPage />
    </AppContextProvider>
  );
});