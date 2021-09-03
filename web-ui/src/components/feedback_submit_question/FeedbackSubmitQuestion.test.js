import React from "react";
import FeedbackSubmitQuestion from "./FeedbackSubmitQuestion";
import {AppContextProvider} from "../../context/AppContext";

it("renders the feedback submit question", () => {
  shallowSnapshot(
      <FeedbackSubmitQuestion
        question="How is the project going so far?"
        questionNumber={1}
      />
  );
});