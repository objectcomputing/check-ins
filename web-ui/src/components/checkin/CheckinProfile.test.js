import React from "react";
import CheckinProfile from "./CheckinProfile";

const state = {
  userProfile: {
    name: "Senior Test",
    memberProfile: {
      pdlId: "",
      role: "Tester",
      workEmail: "test@tester.com",
    },
  },
};

it("renders correctly", () => {
  snapshot(<CheckinProfile state={state} />);
});
