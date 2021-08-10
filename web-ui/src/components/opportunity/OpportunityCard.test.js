import React from "react";
import OpportunityCard from "./OpportunityCard";

const opportunity = {
  description: "Looking for someone to spit some hot fire",
  expiresOn: "01/02/2030",
  name: "Hip hop artist",
  pending: false,
  url: "www.google.com",
};
it("renders correctly", () => {
  snapshot(<OpportunityCard opportunity={opportunity} />);
});
