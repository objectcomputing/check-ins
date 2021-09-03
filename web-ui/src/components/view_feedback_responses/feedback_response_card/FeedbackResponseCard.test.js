import React from "react";
import FeedbackResponseCard from "./FeedbackResponseCard";
import {AppContextProvider} from "../../../context/AppContext";

it("renders correctly", () => {
  shallowSnapshot(
    <AppContextProvider>
      <FeedbackResponseCard
        responderId="01b7d769-9fa2-43ff-95c7-f3b950a27bf9"
        answer="I love opossums. I have rehabilitated baby opossums for 25 years, and I intend to do so until my last day!"
        sentiment={0.8}/>
    </AppContextProvider>
  )
});