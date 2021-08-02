import React from "react";
import AdHocCreationForm from "./AdHocCreationForm";
import { AppContextProvider } from "../../../context/AppContext";


it("renders the feedback tips component.", () => {
  shallowSnapshot(
    <AppContextProvider >
      <AdHocCreationForm/>
      </AppContextProvider >
  );
});