import React from "react";
import PermissionsPage from "./PermissionsPage";
import {AppContextProvider} from "../context/AppContext";

it("renders correctly", () => {
  snapshot(
    <AppContextProvider>
      <PermissionsPage/>
    </AppContextProvider>
  );
});