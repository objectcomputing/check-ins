import React from "react";
import AdminPage from "./AdminPage";
import { AppContextProvider } from "../context/AppContext";

it("renders correctly", () => {
  snapshot(
    <AppContextProvider>
      <AdminPage />
    </AppContextProvider>
  );
});
