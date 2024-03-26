import React from "react";
import FeedbackResponseCard from "./FeedbackResponseCard";
import {AppContextProvider} from "../../../context/AppContext";

import {jest} from "@jest/globals";

jest.mock("@mui/material/Slider", () => (props: any) => {
  const { onChange, 'data-testid': testId, ...rest } = props;

  return (
    <input
      data-testid={testId}
      type="range"
      onChange={event => {
        onChange(null, parseInt(event.target.value, 10));
      }}
      {...rest}
    />
  );
});

it("renders correctly for text responses", () => {
  snapshot(
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
  snapshot(
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
  snapshot(
    <AppContextProvider>
      <FeedbackResponseCard
        responderId="01b7d769-9fa2-43ff-95c7-f3b950a27bf9"
        inputType="SLIDER"
        answer="Neither Agree nor Disagree"
        sentiment={0.5}/>
    </AppContextProvider>
  )
});