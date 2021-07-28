import React from "react";
import { BrowserRouter } from "react-router-dom";
import {AppContextProvider } from "../../../context/AppContext";
import FeedbackRequestSubcard from "./FeedbackRequestSubcard";

it("renders correctly", () => {
  snapshot(
    <BrowserRouter>
      <AppContextProvider>
        <FeedbackRequestSubcard recipientName="test" recipientTitle="test"/>
      </AppContextProvider>
    </BrowserRouter>

  );
});