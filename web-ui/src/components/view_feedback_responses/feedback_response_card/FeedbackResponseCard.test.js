import React from "react";
import FeedbackResponseCard from "./FeedbackResponseCard";
import {AppContextProvider} from "../../../context/AppContext";

it("renders correctly for text responses", () => {
  shallowSnapshot(
    <AppContextProvider>
      <FeedbackResponseCard
        responderId="01b7d769-9fa2-43ff-95c7-f3b950a27bf9"
        inputType="TEXT"
        answer="I love opossums. I have rehabilitated baby opossums for 25 years, and I intend to do so until my last day!"
        sentiment={0.8}/>
    </AppContextProvider>
  )
});

it("renders correctly for radio button responses", () => {
  shallowSnapshot(
    <AppContextProvider>
      <FeedbackResponseCard
        responderId="01b7d769-9fa2-43ff-95c7-f3b950a27bf9"
        inputType="RADIO"
        answer="Yes"
        sentiment={1}/>
    </AppContextProvider>
  )
});

it("renders correctly for slider responses", () => {
  shallowSnapshot(
    <AppContextProvider>
      <FeedbackResponseCard
        responderId="01b7d769-9fa2-43ff-95c7-f3b950a27bf9"
        inputType="SLIDER"
        answer="Neither Agree nor Disagree"
        sentiment={0.5}/>
    </AppContextProvider>
  )
});