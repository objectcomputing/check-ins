import React from "react";
import MemberProfilePage from "./MemberProfilePage";
import { AppContextProvider } from "../context/AppContext";

it("renders correctly", () => {
  <AppContextProvider>
    snapshot(
    <MemberProfilePage />
    );
  </AppContextProvider>;
});
