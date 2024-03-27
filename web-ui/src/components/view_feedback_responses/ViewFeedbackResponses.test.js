import React from "react";
import ViewFeedbackResponses from "./ViewFeedbackResponses";
import {AppContextProvider} from "../../context/AppContext";
import { BrowserRouter } from "react-router-dom";

it("renders correctly", () => {
  snapshot(
    <AppContextProvider>
    <BrowserRouter>
      <ViewFeedbackResponses/>
    </BrowserRouter>
    </AppContextProvider>
  );
});