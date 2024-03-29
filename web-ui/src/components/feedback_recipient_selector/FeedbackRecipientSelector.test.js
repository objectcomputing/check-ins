import React from "react";
import FeedbackRecipientSelector from "./FeedbackRecipientSelector";
import {AppContextProvider} from "../../context/AppContext";
import {BrowserRouter} from "react-router-dom";
import {jest} from '@jest/globals';

describe("FeedbackRecipientSelector", () => {
  it("renders the component", () => {
    snapshot(<BrowserRouter><AppContextProvider><FeedbackRecipientSelector changeQuery={jest.fn()} fromQuery={[]} forQuery="" /></AppContextProvider></BrowserRouter>);
  });
});