import React from "react";
import AnniversaryReportPage from "./AnniversaryReportPage";
import { AppContextProvider } from "../context/AppContext";

it("renders correctly", () => {
  const mockDate = new Date(2022, 1, 1);
  const spy = vi.spyOn(global, "Date").mockImplementation(() => mockDate);

  snapshot(
    <AppContextProvider>
      <AnniversaryReportPage />
    </AppContextProvider>
  );

  spy.mockRestore();
});
