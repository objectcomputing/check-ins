import React from "react";
import FeedbackRecipientSelector from "./FeedbackRecipientSelector";
import {AppContextProvider} from "../../context/AppContext";
import {BrowserRouter} from "react-router-dom";

it("renders the component", () => {
  snapshot(<BrowserRouter><AppContextProvider><FeedbackRecipientSelector /></AppContextProvider></BrowserRouter>);
});