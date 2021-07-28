import React from "react";
import FeedbackResponseCard from "./FeedbackResponseCard";
import {AppContextProvider} from "../../../context/AppContext";

it("renders correctly", () => {
  shallowSnapshot(
    <AppContextProvider>
      <FeedbackResponseCard
        responderName="Job Johnson"
        answer="I love opossums. I have rehabilitated baby opossums for 25 years, and I intend to do so until my last day!"
        sentiment={0.8}/>
    </AppContextProvider>
  )
});