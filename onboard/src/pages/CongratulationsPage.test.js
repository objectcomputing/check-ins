import React from "react";
import CongratulationsPage from "./CongratulationsPage";
import {BrowserRouter} from "react-router-dom"

it("renders correctly", () => {
    snapshot(
      <BrowserRouter>
        <CongratulationsPage />
      </BrowserRouter>
    );
  });