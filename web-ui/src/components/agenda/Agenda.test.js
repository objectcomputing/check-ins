import React from "react";
import Agenda from "./Agenda";
import { AppContextProvider } from "../../context/AppContext";

const initialState = {
  state: {
    userProfile: {
      memberProfile: {
        id: "912834091823",
      },
    },
  },
};

it("renders correctly", () => {
  snapshot(
    <AppContextProvider value={initialState}>
      <Agenda checkinId="394810298371" memberName="mr. test" />
    </AppContextProvider>
  );
});
