import React from "react";
import FeedbackRequestPage from "./FeedbackRequestPage";
import {AppContextProvider} from "../context/AppContext";
import {MemoryRouter} from "react-router-dom";

it("renders correctly", () => {
  snapshot(
      <AppContextProvider>
        <MemoryRouter initialEntries={["/feedback/?for=1234"]} initialIndex={0}>
          <FeedbackRequestPage/>
        </MemoryRouter>
      </AppContextProvider>
  );
});