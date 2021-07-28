import React from "react";
import { BrowserRouter } from "react-router-dom";
import {AppContextProvider } from "../../context/AppContext";
import FeedbackRequestCard from "./FeedbackRequestCard";

it("renders correctly", () => {
  snapshot(
    <BrowserRouter>
      <AppContextProvider>
        <FeedbackRequestCard requesteeName="test" requesteeTitle="test" templateName="test"/>
      </AppContextProvider>
    </BrowserRouter>

  );
});