import React from "react";
import { AppContextProvider } from "../../context/AppContext";
import GuidesPanel from "./GuidesPanel";

it("renders correctly for members", () => {
  snapshot(
    <AppContextProvider>
      <GuidesPanel role="MEMBER" title="Team Member Resources" />
    </AppContextProvider>);
});

it("renders correctly for PDLs", () => {
  snapshot(
    <AppContextProvider>
      <GuidesPanel role="PDL" title="Development Lead Guides" />
    </AppContextProvider>
  );
});

it("renders correctly for admins", () => {
  snapshot(
    <AppContextProvider>
      <GuidesPanel role="ADMIN" title="Admin Documents" />
    </AppContextProvider>
  );
});
