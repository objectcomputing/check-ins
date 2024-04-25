import React from "react";
import CheckinProfile from "./CheckinProfile";
import { AppContextProvider } from "../../context/AppContext";

const initialState = {
  state: {
    selectedProfile: {},
    userProfile: {
      name: "Senior Test",
      memberProfile: {
        pdlId: "",
        title: "Tester",
        workEmail: "test@tester.com",
      },
      role: ["MEMBER"],
    },
  },
};

it("renders correctly", () => {
  snapshot(
    <AppContextProvider value={initialState}>
      <CheckinProfile />
    </AppContextProvider>
  );
});
