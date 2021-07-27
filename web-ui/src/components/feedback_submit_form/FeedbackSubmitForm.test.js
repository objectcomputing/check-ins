import React from "react";
import FeedbackSubmitForm from "./FeedbackSubmitForm";

it("renders the feedback submit form", async () => {
  shallowSnapshot(<FeedbackSubmitForm requesteeName="John Doe"/>);
});