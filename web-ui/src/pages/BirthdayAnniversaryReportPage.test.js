import React from "react";
import BirthdayAnniversaryReportPage from "./BirthdayAnniversaryReportPage";
import { AppContextProvider } from "../context/AppContext";

it("renders correctly", () => {
  snapshot(
    <AppContextProvider>
      <BirthdayAnniversaryReportPage />
    </AppContextProvider>
  );
});
