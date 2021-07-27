import React from "react";
import FeedbackSubmitForm from "./FeedbackSubmitForm";

it("renders the feedback submit form", async () => {
  shallowSnapshot(<FeedbackSubmitForm requesteeName="John Doe" requestId={'ab7b21d4-f88c-4494-9b0b-8541636025eb'}/>);
});