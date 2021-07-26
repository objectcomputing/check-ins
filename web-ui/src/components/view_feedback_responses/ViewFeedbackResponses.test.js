import React from "react";
import ViewFeedbackResponses from "./ViewFeedbackResponses";
import {AppContextProvider} from "../../context/AppContext";

it("renders correctly", () => {
  shallowSnapshot(
    <AppContextProvider>
      <ViewFeedbackResponses/>
    </AppContextProvider>
  );
});