import React from "react";
import WebPortal from "./WebPortal";
import {BrowserRouter} from "react-router-dom";

it("renders correctly", () => {
  snapshot(
    <BrowserRouter>
        <WebPortal />
    </BrowserRouter>
);
});
