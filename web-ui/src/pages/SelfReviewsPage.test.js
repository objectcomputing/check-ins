import React from "react";
import SelfReviewsPage from "./SelfReviewsPage";
import {AppContextProvider} from "../context/AppContext";
import {MemoryRouter} from "react-router-dom";

it("SelfReviewsPage renders correctly", () => {
  snapshot(
      <AppContextProvider>
        <MemoryRouter initialEntries={["/feedback/self-reviews"]} initialIndex={0}>
          <SelfReviewsPage/>
        </MemoryRouter>
      </AppContextProvider>
  );
});