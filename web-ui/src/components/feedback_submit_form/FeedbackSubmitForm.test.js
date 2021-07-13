import React from "react";
import FeedbackSubmitForm from "./FeedbackSubmitForm";

it("renders the feedback submit form", () => {
  shallowSnapshot(<FeedbackSubmitForm requesteeName="John Doe"/>);
});