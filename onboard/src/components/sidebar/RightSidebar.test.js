import React from "react";
import RightSidebar from "./RightSidebar";
import { BrowserRouter } from "react-router-dom"

it("renders correctly", () => {
    snapshot(
        <BrowserRouter>
            <RightSidebar />
        </BrowserRouter>
    );
}); 