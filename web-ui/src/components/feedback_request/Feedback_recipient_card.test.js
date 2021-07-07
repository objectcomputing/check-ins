import React from "react";
import FeedbackRecipientCard from "./Feedback_recipient_card";
import {AppContextProvider} from "../../context/AppContext";
import {BrowserRouter} from "react-router-dom";

it("renders the recipient card", () => {

  snapshot(
    <AppContextProvider>
      <BrowserRouter>
        <FeedbackRecipientCard/>
      </BrowserRouter>
    </AppContextProvider>
    );
});