import React from "react";
import FeedbackRequestPage from "./FeedbackRequestPage";
import {AppContextProvider} from "../context/AppContext";

it("renders correctly", () => {
  snapshot(
    <AppContextProvider>
      <FeedbackRequestPage/>
    </AppContextProvider>
  );
});