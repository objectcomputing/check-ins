import React from "react";
import CheckinsPage from "./CheckinsPage";
import { AppContextProvider } from "../context/AppContext";

it("renders correctly", () => {
    <AppContextProvider>
        snapshot(
        <CheckinsPage />
        );
    </AppContextProvider>;
});