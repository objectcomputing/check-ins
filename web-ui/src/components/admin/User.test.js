import React from "react";
import User from "./User";
import { AppContextProvider } from "../../context/AppContext";

it("renders correctly", () => {
  snapshot(
    <AppContextProvider>
      <User />
    </AppContextProvider>
  );
});
