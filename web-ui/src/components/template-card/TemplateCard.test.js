import React from "react";
import TemplateCard from "./TemplateCard";
import { AppContextProvider } from "../../context/AppContext";

it("renders correctly", () => {
    snapshot(
        <AppContextProvider >
            <TemplateCard />
        </AppContextProvider>
    );
});