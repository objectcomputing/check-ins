import React from "react";
import UserPage from "./UserPage";
import { AppContextProvider } from "../context/AppContext";

it("renders correctly", () => {
  snapshot(
    <AppContextProvider>
      <UserPage />
    </AppContextProvider>
  );
});
