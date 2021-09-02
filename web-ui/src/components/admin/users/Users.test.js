import React from "react";
import Users from "./Users";
import { AppContextProvider } from "../../../context/AppContext";

it("renders correctly", () => {
  snapshot(
    <AppContextProvider>
      <Users />
    </AppContextProvider>
  );
});
