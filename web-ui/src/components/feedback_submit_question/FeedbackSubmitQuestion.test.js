import React from "react";
import FeedbackSubmitQuestion from "./FeedbackSubmitQuestion";
import {AppContextProvider} from "../../context/AppContext";

it("renders the feedback submit question as text", () => {
  snapshot(
    <AppContextProvider value={{ state: {} }}>
      <FeedbackSubmitQuestion
        question={{
          id: "1",
          question: "How is the project going so far?",
          questionNumber: 1,
          questionType: "TEXT"
        }}
        readOnly={false}
        answer={{
          id: "a",
          answer: "Good",
          questionId: "1",
          requestId: "abc"
        }}
        requestId="abc"
        onAnswerChange={jest.fn()}
      />
    </AppContextProvider>
  );
});

it("renders the feedback submit question as radio buttons", () => {
  snapshot(
    <AppContextProvider value={{ state: {} }}>
      <FeedbackSubmitQuestion
        question={{
          id: "1",
          question: "Do you think the project is going well so far?",
          questionNumber: 1,
          questionType: "RADIO"
        }}
        readOnly={false}
        answer={{
          id: "a",
          answer: "Yes",
          questionId: "1",
          requestId: "abc"
        }}
        requestId="abc"
        onAnswerChange={jest.fn()}
      />
    </AppContextProvider>
  );
});

it("renders the feedback submit question as a slider", () => {
  snapshot(
    <AppContextProvider value={{ state: {} }}>
      <FeedbackSubmitQuestion
        question={{
          id: "1",
          question: "Do you think the project is going well so far?",
          questionNumber: 1,
          questionType: "SLIDER"
        }}
        readOnly={false}
        answer={{
          id: "a",
          answer: "Neither Agree nor Disagree",
          questionId: "1",
          requestId: "abc"
        }}
        requestId="abc"
        onAnswerChange={jest.fn()}
      />
    </AppContextProvider>
  );
});