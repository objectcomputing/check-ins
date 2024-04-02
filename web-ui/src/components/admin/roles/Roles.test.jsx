import React from "react";
import Roles from "./Roles";
import { AppContextProvider } from "../../../context/AppContext";

const initialState = {
  state: {
    memberProfiles: [
      { id: 1, name: "Señior Test" },
      { id: 2, name: "Señora Test" },
      { id: 3, name: "Herr Test" },
    ],
    roles: [
      { id: 1, role: "ADMIN", memberid: 1 },
      { id: 2, role: "PDL", memberid: 2 },
    ],
  },
};

it("renders correctly", () => {
  snapshot(
    <AppContextProvider value={initialState}>
      <Roles />
    </AppContextProvider>
  );
});
