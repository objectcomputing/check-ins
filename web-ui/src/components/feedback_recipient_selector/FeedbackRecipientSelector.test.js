import React, {useContext} from "react";
import FeedbackRecipientSelector from "./FeedbackRecipientSelector";
import {AppContext, AppContextProvider} from "../../context/AppContext";
import {BrowserRouter} from "react-router-dom";

it("renders the component", () => {
  snapshot(<BrowserRouter><AppContextProvider><FeedbackRecipientSelector /></AppContextProvider></BrowserRouter>);
});