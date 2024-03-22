import React from "react";
import EditPermissionsPage from "./EditPermissionsPage";
import { AppContextProvider } from "../context/AppContext";

it("renders correctly", () => {
  snapshot(
    <AppContextProvider>
      <EditPermissionsPage />
    </AppContextProvider>
  );
});
