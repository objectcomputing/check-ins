import React from "react";
import FeedbackRecipientCard from "./Feedback_recipient_card";
import {AppContextProvider} from "../../context/AppContext";


it("renders the recipient card", () => {
  snapshot(<AppContextProvider><FeedbackRecipientCard/></AppContextProvider>);
});