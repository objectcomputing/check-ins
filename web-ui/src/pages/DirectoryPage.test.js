import React from "react";
import DirectoryPage from "./DirectoryPage";
import { AppContextProvider } from "../context/AppContext";

it("renders correctly", () => {
    <AppContextProvider>
        snapshot(
        <DirectoryPage />
        );
    </AppContextProvider>;
});