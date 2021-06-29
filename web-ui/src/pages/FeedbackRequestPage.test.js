import React from "react";
import FeedbackRequestPage from "./FeedbackRequestPage";
import {AppContextProvider} from "../context/AppContext";
import {BrowserRouter} from "react-router-dom";

it("renders correctly", () => {
  snapshot(
      <AppContextProvider>˚
        <BrowserRouter>
          <FeedbackRequestPage/>
        </BrowserRouter>
      </AppContextProvider>
  );
});