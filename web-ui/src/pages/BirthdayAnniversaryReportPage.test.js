import React from "react";
import BirthdayAnniversaryReportPage from "./BirthdayAnniversaryReportPage";
import { AppContextProvider } from "../context/AppContext";

it("renders correctly", () => {
  const mockDate = new Date(2022, 1, 1)
  const spy = jest
    .spyOn(global, 'Date')
    .mockImplementation(() => mockDate)

  snapshot(
    <AppContextProvider>
      <BirthdayAnniversaryReportPage />
    </AppContextProvider>
  );

  spy.mockRestore()
});
