import React from "react";
import { BrowserRouter } from "react-router-dom";
import WebPortal from "./WebPortal";

it("renders correctly", () => {
  snapshot(
    <BrowserRouter>
      <WebPortal />
    </BrowserRouter>
  );
});
